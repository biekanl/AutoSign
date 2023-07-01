package cn.unbug.autosign.service;

import cn.unbug.autosign.entity.SignHistory;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 签到记录表 服务类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
public interface SignHistoryService extends IService<SignHistory> {

    /**
     * 获取最近一次打卡
     * @param id
     * @return
     */
    SignHistory queryLatestHistory(String id);

    /**
     * 获取历史记录
     * @param pageNo
     * @param pageSize
     * @param accountNumber
     * @return
     */
    IPage<SignHistory> queryHistoryByPage(Integer pageNo, Integer pageSize, String accountNumber);
}
