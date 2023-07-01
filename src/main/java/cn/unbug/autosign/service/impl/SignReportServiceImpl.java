package cn.unbug.autosign.service.impl;

import cn.unbug.autosign.entity.SignReport;
import cn.unbug.autosign.entity.SysUser;
import cn.unbug.autosign.mapper.SignReportMapper;
import cn.unbug.autosign.service.SignReportService;
import cn.unbug.autosign.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 签到汇报表 服务实现类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-23
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SignReportServiceImpl extends ServiceImpl<SignReportMapper, SignReport> implements SignReportService {


    /**
     * 获取id列表
     *
     * @param code
     * @return
     */
    @Override
    public List<String> obtainTheIdList(String superior, String code) {
        LambdaQueryWrapper<SignReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignReport::getReportCode, code).eq(SignReport::getSuperior, superior);
        List<SignReport> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(SignReport::getId).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    /**
     * 添加 汇报
     *
     * @param signReport
     */
    @Override
    public void addSignReport(SignReport signReport) {
        SysUser sysUser = SecurityUtils.loginUser();
        signReport.setSuperior(sysUser.getPhone());
        this.saveOrUpdate(signReport);
    }

    /**
     * 修改 汇报
     *
     * @param signReport
     */
    @Override
    public void editSignReport(SignReport signReport) {
        this.saveOrUpdate(signReport);
    }

    /**
     * 删除 汇报
     *
     * @param id
     */
    @Override
    public void deleteSignReport(String id) {
        this.removeById(id);
    }

    /**
     * 获取汇报列表
     *
     * @param pageNo
     * @param pageSize
     * @param code
     * @return
     */
    @Override
    public IPage<SignReport> queryByPage(Integer pageNo, Integer pageSize, String code) {
        LambdaQueryWrapper<SignReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignReport::getReportCode, code);
        wrapper.orderByDesc(SignReport::getUpdatedTime);
        wrapper.eq(SignReport::getSuperior, SecurityUtils.loginUser().getPhone());
        IPage<SignReport> queryPage = new Page<>(pageNo, pageSize);
        this.page(queryPage, wrapper);
        log.info("=== SignReportServiceImpl queryByPage {} ===", JSON.toJSONString(queryPage));
        return queryPage;
    }
}
