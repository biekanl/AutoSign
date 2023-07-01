package cn.unbug.autosign.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.MD5;
import cn.unbug.autosign.config.Constants;
import cn.unbug.autosign.config.ServletUtils;
import cn.unbug.autosign.config.ThreadLocalUtils;
import cn.unbug.autosign.config.exception.ServiceException;
import cn.unbug.autosign.config.redis.RedisUtils;
import cn.unbug.autosign.entity.SignInUser;
import cn.unbug.autosign.entity.SysUser;
import cn.unbug.autosign.entity.vo.SysUserVo;
import cn.unbug.autosign.mapper.SysUserMapper;
import cn.unbug.autosign.service.SignInUserService;
import cn.unbug.autosign.service.SysUserService;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author zhangtao
 * @since 2023-05-30
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private RedisUtils redisUtils;


    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    private SignInUserService signInUserService;


    /**
     * 获取代理列表
     *
     * @param pageNo
     * @param pageSize
     * @param phone
     * @return
     */
    @Override
    public IPage<SysUser> queryByPage(Integer pageNo, Integer pageSize, String phone) {
        log.info("=== SysUserServiceImpl queryByPage pageNo :{} pageSize :{} phone :{} ===", pageNo, pageSize, phone);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(phone)) {
            wrapper.like(SysUser::getPhone, "%".concat(phone).concat("%"));
        }
        wrapper.orderByDesc(SysUser::getUpdatedTime);
        SysUser sysUser = SecurityUtils.loginUser();
        wrapper.eq(SysUser::getSuperior, sysUser.getPhone());
        IPage<SysUser> page = new Page<>(pageNo, pageSize);
        return this.page(page, wrapper);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public SysUserVo getUserInfo() {
        SysUser sysUser = SecurityUtils.loginUser();
        SysUserVo sysUserVo = new SysUserVo();
        BeanUtil.copyProperties(sysUser, sysUserVo);
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getSuperior, sysUser.getPhone());
        // 代理
        sysUserVo.setProxys(this.count(userWrapper));
        LambdaQueryWrapper<SignInUser> signWrapper = new LambdaQueryWrapper<>();
        signWrapper.eq(SignInUser::getSuperior, sysUser.getPhone());
        sysUserVo.setCustomer(signInUserService.count(signWrapper));
        log.info("=== SysUserServiceImpl getUserInfo sysUser :{} ===", JSON.toJSONString(sysUserVo));
        return sysUserVo;
    }

    /**
     * 登录
     *
     * @param sysUser
     * @return
     */
    @Override
    public String login(SysUser sysUser) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, sysUser.getPhone());
        List<SysUser> sysUsers = this.list(wrapper);
        if (CollectionUtils.isEmpty(sysUsers)) {
            throw new ServiceException(500, "手机号/密码错误！");
        }
        if (!StringUtils.equals(MD5.create().digestHex(sysUser.getPassWord() + Constants.SALT), sysUsers.get(0).getPassWord())) {
            throw new ServiceException(500, "手机号/密码错误！");
        }
        if (StringUtils.equals(Constants.STATUS_1, sysUsers.get(0).getStatus())) {
            throw new ServiceException(500, "账号已锁定！");
        }
        String token = IdUtil.fastSimpleUUID();
        log.info("=== SysUserServiceImpl login :{} ===", token);
        redisUtils.set(Constants.SYS_USER_GROUP + token, sysUsers.get(0), Constants.MILLIS_MINUTE_TEN);
        return token;
    }

    /**
     * 退出
     */
    @Override
    public void loginout() {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = request.getHeader(Constants.SYS_TOKEN);
        if (StringUtils.isNotBlank(token)) {
            redisUtils.del(Constants.SYS_USER_GROUP + token);
        }
    }

    /**
     * 添加代理
     *
     * @param sysUser
     */
    @Override
    public void addUser(SysUser sysUser) {
        SysUser loginUser = SecurityUtils.loginUser();
        if (loginUser.getProxy() != 0) {
            throw new ServiceException("无添加代理权限请联系管理员！");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, sysUser.getPhone());
        int count = this.count(wrapper);
        if (count != 0) {
            throw new ServiceException(500, "用户已存在!");
        }
        sysUser.setPassWord(Constants.DEFAULT_PASSWORD);
        sysUser.setStatus(Constants.STATUS_0);
        sysUser.setSuperior(loginUser.getPhone());
        boolean save = this.save(sysUser);
        log.info("=== SysUserServiceImpl addUser save :{} ===", save);
        if (save && StringUtils.isNotBlank(sysUser.getPushToken())) {
            threadPoolTaskExecutor.submit(() -> {
                MessageUtils.sendMsg(sysUser.getPushToken(), "用户通知", "账号添加成功,账号为手机号,默认密码：123456");
            });
        }
    }

    /**
     * 修改密码
     *
     * @param passWord
     */
    @Override
    public void changePassword(String passWord) {
        String pw = MD5.create().digestHex(passWord + Constants.SALT);
        SysUser sysUser = ThreadLocalUtils.get(Constants.SYS_USER);
        sysUser.setPassWord(pw);
        boolean update = this.updateById(sysUser);
        log.info("=== SysUserServiceImpl changePassword update :{} ===", update);
        if (update && StringUtils.isNotBlank(sysUser.getPushToken())) {
            threadPoolTaskExecutor.submit(() -> {
                MessageUtils.sendMsg(sysUser.getPushToken(), "用户通知", "密码修改成功！");
            });
        }
    }

    /**
     * 修改在状态
     *
     * @param id
     */
    @Override
    public String changeStatus(String id) {
        SysUser loginUser = SecurityUtils.loginUser();
        if (loginUser.getProxy() != 0) {
            throw new ServiceException("无更新状态权限请联系管理员！");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getId, id).eq(SysUser::getSuperior, loginUser.getPhone());
        SysUser sysUser = this.getOne(wrapper);
        if (Objects.isNull(sysUser)) {
            throw new ServiceException("未找到用户！");
        }
        if (StringUtils.equals(sysUser.getStatus(), Constants.STATUS_1)) {
            sysUser.setStatus(Constants.STATUS_0);
        } else if (StringUtils.equals(sysUser.getStatus(), Constants.STATUS_0)) {
            sysUser.setStatus(Constants.STATUS_1);
        }
        boolean update = this.updateById(sysUser);
        if (update && StringUtils.isNotBlank(sysUser.getPushToken())) {
            threadPoolTaskExecutor.submit(() -> {
                MessageUtils.sendMsg(sysUser.getPushToken(), "用户通知", StringUtils.equals(sysUser.getStatus(), Constants.STATUS_1) ? "账号已锁定" : "账号状态已正常");
            });
        }
        return sysUser.getStatus();
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public SysUser selectUserById(String id) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        SysUser loginUser = SecurityUtils.loginUser();
        wrapper.eq(SysUser::getId, id).eq(SysUser::getSuperior, loginUser.getPhone());
        SysUser sysUser = this.getOne(wrapper);
        if (Objects.isNull(sysUser)) {
            throw new ServiceException("未找到用户！");
        }
        return sysUser;
    }

    /**
     * 更新用户
     *
     * @param sysUser
     */
    @Override
    public void updateUser(SysUser sysUser) {
        SysUser loginUser = SecurityUtils.loginUser();
        if (loginUser.getProxy() != 0) {
            throw new ServiceException("无更新代理权限请联系管理员！");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getId, sysUser.getId()).eq(SysUser::getSuperior, loginUser.getPhone());
        SysUser _sysUser = this.getOne(wrapper);
        if (Objects.isNull(_sysUser)) {
            throw new ServiceException("未找到用户！");
        }
        boolean update = this.updateById(sysUser);
        if (update && StringUtils.isNotBlank(sysUser.getPushToken())) {
            threadPoolTaskExecutor.submit(() -> {
                MessageUtils.sendMsg(sysUser.getPushToken(), "用户通知", "账号信息已更新！");
            });
        }
    }

    /**
     * 根据id删除用户
     *
     * @param id
     */
    @Override
    public void deleteUserById(String id) {
        SysUser loginUser = SecurityUtils.loginUser();
        if (loginUser.getProxy() != 0) {
            throw new ServiceException("无删除用户权限请联系管理员！");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getId, id).eq(SysUser::getSuperior, loginUser.getPhone());
        SysUser sysUser = this.getOne(wrapper);
        if (Objects.isNull(sysUser)) {
            throw new ServiceException("未找到用户！");
        }
        boolean remove = this.removeById(id);
        if (remove && StringUtils.isNotBlank(sysUser.getPushToken())) {
            threadPoolTaskExecutor.submit(() -> {
                MessageUtils.sendMsg(sysUser.getPushToken(), "用户通知", "账号信息已被删除！");
            });
        }
    }
}
