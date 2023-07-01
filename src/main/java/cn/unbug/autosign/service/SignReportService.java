package cn.unbug.autosign.service;

import cn.unbug.autosign.entity.SignReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 签到汇报表 服务类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-23
 */
public interface SignReportService extends IService<SignReport> {

    /**
     * 获取id列表
     * @param code
     * @return
     */
    List<String> obtainTheIdList(String superior,String code);

    /**
     * 添加 汇报
     * @param signReport
     */
    void addSignReport(SignReport signReport);

    /**
     * 修改 汇报
     * @param signReport
     */
    void editSignReport(SignReport signReport);

    /**
     * 删除 汇报
     * @param id
     */
    void deleteSignReport(String id);

    /**
     * 获取汇报列表
     *
     * @param pageNo
     * @param pageSize
     * @param code
     * @return
     */
    IPage<SignReport> queryByPage(Integer pageNo, Integer pageSize, String code);
}
