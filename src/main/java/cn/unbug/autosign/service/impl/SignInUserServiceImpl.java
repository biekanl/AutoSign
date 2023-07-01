package cn.unbug.autosign.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.unbug.autosign.config.Constants;
import cn.unbug.autosign.config.exception.ServiceException;
import cn.unbug.autosign.config.redis.RedisUtils;
import cn.unbug.autosign.entity.CheckInTime;
import cn.unbug.autosign.entity.SignHistory;
import cn.unbug.autosign.entity.SignInUser;
import cn.unbug.autosign.entity.SysUser;
import cn.unbug.autosign.entity.vo.SignInUserVo;
import cn.unbug.autosign.mapper.SignInUserMapper;
import cn.unbug.autosign.mapper.SysUserMapper;
import cn.unbug.autosign.service.CheckInTimeService;
import cn.unbug.autosign.service.SignHistoryService;
import cn.unbug.autosign.service.SignInUserService;
import cn.unbug.autosign.utils.MessageUtils;
import cn.unbug.autosign.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 签到用户 服务实现类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SignInUserServiceImpl extends ServiceImpl<SignInUserMapper, SignInUser> implements SignInUserService {

    public static final String DAILY = "daily";

    private static final String WEEKLY = "weekly";

    private static final String MONTHLY = "monthly";

    public static final String SWITCH_0 = "0";


    public static final String SWITCH_1 = "1";

    public static final String USER_NOT_FOUND = "未找到用户！";

    private SignInUserMapper signInUserMapper;


    private SysUserMapper sysUserMapper;


    private CheckInTimeService checkInTimeService;


    private SignHistoryService signHistoryService;


    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    private RedisUtils redisUtils;


    @Override
    public void addSignInUser(SignInUserVo signInUser) {
        SysUser loginUser = SecurityUtils.loginUser();
        SysUser sysUser = sysUserMapper.selectById(loginUser.getId());
        if (sysUser.getProxy() == 1 && sysUser.getNumberOfConsumptions() == 0) {
            throw new ServiceException("可添加打卡用户数已用完！");
        }
        log.info("=== SignInUserServiceImpl addSignInUser signInUser :{} ===", JSON.toJSONString(signInUser));
        // 检查账号是否存在
        SignInUser user = signInUserMapper.selectUserByAccountNumber(signInUser.getAccountNumber());
        if (Objects.nonNull(user)) {
            log.info("=== SignInUserServiceImpl addSignInUser user :{}  ===", JSON.toJSONString(user));
            throw new ServiceException(500, "该账号已存在！");
        }
        signInUser.setStatus(Constants.STATUS_0);
        signInUser.setDeviceId(UUID.randomUUID().toString(true));
        signInUser.setSuperior(loginUser.getPhone());
        BigDecimal latitude = new BigDecimal(signInUser.getLatitude()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        signInUser.setLatitude(latitude.toString());
        BigDecimal longitude = new BigDecimal(signInUser.getLongitude()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        signInUser.setLongitude(longitude.toString());
        log.info("=== SignInUserServiceImpl addSignInUser :{} ===", JSON.toJSONString(signInUser));
        signInUser.setDaily(SWITCH_0);
        signInUser.setWeekly(SWITCH_0);
        signInUser.setMonthly(SWITCH_0);
        this.save(signInUser);
        sysUser.setNumberOfConsumptions(sysUser.getNumberOfConsumptions() - 1);
        sysUserMapper.updateById(sysUser);
        // 保存打卡周期
        List<String> punchCycles = signInUser.getPunchCycles();
        for (String punchCycle : punchCycles) {
            CheckInTime checkInTime = new CheckInTime();
            checkInTime.setSignId(signInUser.getId());
            checkInTime.setWeek(punchCycle);
            checkInTimeService.save(checkInTime);
        }
        // 打卡数据
        List<SignInUser> signInUsers = this.inquireAboutCustomersToPunchIn(signInUser.getId());
        this.sign(signInUsers);
    }

    /**
     * 分页查询客户
     *
     * @param pageNo
     * @param pageSize
     * @param accountNumber
     * @return
     */
    @Override
    public IPage<SignInUserVo> queryByPage(Integer pageNo, Integer pageSize, String accountNumber) {
        log.info("=== SignInUserServiceImpl queryByPage pageNo :{} pageSize :{} accountNumber :{} ===", pageNo, pageSize, accountNumber);
        LambdaQueryWrapper<SignInUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(accountNumber)) {
            wrapper.like(SignInUser::getAccountNumber, "%".concat(accountNumber).concat("%"));
        }
        wrapper.orderByDesc(SignInUser::getUpdatedTime);
        wrapper.eq(SignInUser::getSuperior, SecurityUtils.loginUser().getPhone());
        IPage<SignInUser> queryPage = new Page<>(pageNo, pageSize);
        this.page(queryPage, wrapper);
        IPage<SignInUserVo> page = new Page<>(pageNo, pageSize);
        BeanUtils.copyProperties(queryPage, page);
        List<SignInUserVo> signInUserVoList = Lists.newArrayList();
        for (SignInUser signInUser : page.getRecords()) {
            List<String> punchCycles = checkInTimeService.queryWeekBySignId(signInUser.getId());
            SignInUserVo signInUserVo = new SignInUserVo();
            BeanUtils.copyProperties(signInUser, signInUserVo);
            signInUserVo.setPunchCycle(dataConversion(punchCycles));
            signInUserVo.setPunchCycles(punchCycles);
            SignHistory signHistory = signHistoryService.queryLatestHistory(signInUser.getId());
            if (Objects.nonNull(signHistory)) {
                signInUserVo.setLatestTime(signHistory.getCreatedTime());
            }
            signInUserVoList.add(signInUserVo);
        }
        page.setRecords(signInUserVoList);
        return page;
    }

    /**
     * 数据转换
     *
     * @param punchCycles
     * @return
     */
    private String dataConversion(List<String> punchCycles) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(punchCycles)) {
            if (punchCycles.contains("0")) {
                sb.append(" 周一");
            }
            if (punchCycles.contains("1")) {
                sb.append(" 周二");
            }
            if (punchCycles.contains("2")) {
                sb.append(" 周三");
            }
            if (punchCycles.contains("3")) {
                sb.append(" 周四");
            }
            if (punchCycles.contains("4")) {
                sb.append(" 周五");
            }
            if (punchCycles.contains("5")) {
                sb.append(" 周六");
            }
            if (punchCycles.contains("6")) {
                sb.append(" 周日");
            }
        }
        return sb.toString();
    }

    /**
     * 修改状态
     *
     * @param id
     */
    @Override
    public String changeStatus(String id) {
        LambdaQueryWrapper<SignInUser> wrapper = new LambdaQueryWrapper<>();
        SysUser loginUser = SecurityUtils.loginUser();
        wrapper.eq(SignInUser::getId, id).eq(SignInUser::getSuperior, loginUser.getPhone());
        SignInUser signInUser = this.getOne(wrapper);
        if (Objects.isNull(signInUser)) {
            throw new ServiceException(USER_NOT_FOUND);
        }
        if (StringUtils.equals(signInUser.getStatus(), Constants.STATUS_1)) {
            signInUser.setStatus(Constants.STATUS_0);
        } else if (StringUtils.equals(signInUser.getStatus(), Constants.STATUS_0)) {
            //删除打卡数据
            redisUtils.del(Constants.SIGN_USER_GROUP + signInUser.getId());
            signInUser.setStatus(Constants.STATUS_1);
        }
        boolean update = this.updateById(signInUser);
        if (update && StringUtils.isNotBlank(signInUser.getPushToken())) {
            threadPoolTaskExecutor.submit(() -> {
                MessageUtils.sendMsg(signInUser.getPushToken(), "用户通知", StringUtils.equals(signInUser.getStatus(), Constants.STATUS_1) ? "账号已锁定" : "账号状态已正常");
            });
        }
        // 打卡数据
        List<SignInUser> signInUsers = this.inquireAboutCustomersToPunchIn(signInUser.getId());
        this.sign(signInUsers);
        return signInUser.getStatus();
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public SignInUserVo selectUserById(String id) {
        LambdaQueryWrapper<SignInUser> wrapper = new LambdaQueryWrapper<>();
        SysUser loginUser = SecurityUtils.loginUser();
        wrapper.eq(SignInUser::getId, id).eq(SignInUser::getSuperior, loginUser.getPhone());
        SignInUser signInUser = this.getOne(wrapper);
        if (Objects.isNull(signInUser)) {
            throw new ServiceException(USER_NOT_FOUND);
        }
        SignInUserVo signInUserVo = new SignInUserVo();
        BeanUtils.copyProperties(signInUser, signInUserVo);
        List<String> punchCycles = checkInTimeService.queryWeekBySignId(signInUser.getId());
        signInUserVo.setPunchCycles(punchCycles);
        return signInUserVo;
    }

    /**
     * 更新用户
     *
     * @param signInUser
     */
    @Override
    public void updateUser(SignInUserVo signInUser) {
        LambdaQueryWrapper<SignInUser> wrapper = new LambdaQueryWrapper<>();
        SysUser loginUser = SecurityUtils.loginUser();
        wrapper.eq(SignInUser::getId, signInUser.getId()).eq(SignInUser::getSuperior, loginUser.getPhone());
        SignInUser _signInUser = this.getOne(wrapper);
        if (Objects.isNull(_signInUser)) {
            throw new ServiceException(USER_NOT_FOUND);
        }
        BigDecimal latitude = new BigDecimal(signInUser.getLatitude()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        signInUser.setLatitude(latitude.toString());
        BigDecimal longitude = new BigDecimal(signInUser.getLongitude()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        signInUser.setLongitude(longitude.toString());
        boolean update = this.updateById(signInUser);
        if (update) {
            //删除打卡周期
            checkInTimeService.deleteWeekBySignId(signInUser.getId());
            for (String punchCycle : signInUser.getPunchCycles()) {
                CheckInTime checkInTime = new CheckInTime();
                checkInTime.setSignId(signInUser.getId());
                checkInTime.setWeek(punchCycle);
                checkInTimeService.save(checkInTime);
            }
        }
        if (update && StringUtils.isNotBlank(signInUser.getPushToken())) {
            threadPoolTaskExecutor.submit(() -> {
                MessageUtils.sendMsg(signInUser.getPushToken(), "用户通知", "账号信息已更新！");
            });
        }
        // 打卡数据
        List<SignInUser> signInUsers = this.inquireAboutCustomersToPunchIn(signInUser.getId());
        this.sign(signInUsers);
    }

    /**
     * 根据id删除用户
     *
     * @param id
     */
    @Override
    public void deleteUserById(String id) {
        LambdaQueryWrapper<SignInUser> wrapper = new LambdaQueryWrapper<>();
        SysUser loginUser = SecurityUtils.loginUser();
        wrapper.eq(SignInUser::getId, id).eq(SignInUser::getSuperior, loginUser.getPhone());
        SignInUser signInUser = this.getOne(wrapper);
        if (Objects.isNull(signInUser)) {
            throw new ServiceException(USER_NOT_FOUND);
        }
        boolean remove = this.removeById(id);
        if (remove) {
            //删除数据
            redisUtils.del(Constants.SIGN_USER_GROUP + signInUser.getId());
            if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                threadPoolTaskExecutor.submit(() -> {
                    MessageUtils.sendMsg(signInUser.getPushToken(), "用户通知", "账号信息已被删除！");
                });
            }
        }
    }

    /**
     * 查询待签约的客户
     *
     * @return
     */
    @Override
    public List<SignInUser> inquireAboutCustomersToPunchIn(String id) {
        return signInUserMapper.inquireAboutCustomersToPunchIn(id);
    }


    /**
     * 设置打卡
     *
     * @param signInUsers
     */
    @Override
    public void sign(List<SignInUser> signInUsers) {
        log.info("=== SignInUserServiceImpl sign signInUsers :{} ===", signInUsers.size());
        if (CollectionUtils.isNotEmpty(signInUsers)) {
            for (SignInUser signInUser : signInUsers) {
                try {
                    String clockInTime = DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT) + " " + signInUser.getClockInTime();
                    log.info("=== SignInUserServiceImpl sign accountNumber :{}  clockInTime :{} ===", signInUser.getAccountNumber(), clockInTime);
                    long between = DateUtil.between(new Date(), DateUtil.parse(clockInTime, DatePattern.NORM_DATETIME_MINUTE_PATTERN), DateUnit.SECOND, false);
                    if (between < 0) {
                        continue;
                    }
                    redisUtils.set(Constants.SIGN_USER_GROUP + signInUser.getId(), signInUser, between);
                } catch (Exception e) {
                    log.info("===SignInUserServiceImpl sign signInUser :{} ===", JSON.toJSONString(signInUser));
                    log.error("=== SignInUserServiceImpl sign exception ===", e);
                }
            }
        }
    }

    /**
     * 获取日报用户
     *
     * @return
     */
    @Override
    public List<SignInUser> dailyReportJob() {
        return signInUserMapper.queryReportJobByCode(DAILY);
    }

    /**
     * 获取周报用户
     *
     * @return
     */
    @Override
    public List<SignInUser> weeklyReportJob() {
        return signInUserMapper.queryReportJobByCode(WEEKLY);
    }

    /**
     * 获取月报用户
     *
     * @return
     */
    @Override
    public List<SignInUser> monthlyReportJob() {
        return signInUserMapper.queryReportJobByCode(MONTHLY);
    }
}
