package cn.unbug.autosign.service;

import cn.unbug.autosign.entity.CheckInTime;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 签到时间 服务类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-07
 */
public interface CheckInTimeService extends IService<CheckInTime> {

    /**
     *  查询打卡周期
     * @param id
     * @return
     */
    List<String> queryWeekBySignId(String id);

    /**
     * 删除打卡周期
     * @param id
     */
    void deleteWeekBySignId(String id);
}
