package cn.unbug.autosign.service.impl;

import cn.unbug.autosign.entity.CheckInTime;
import cn.unbug.autosign.mapper.CheckInTimeMapper;
import cn.unbug.autosign.service.CheckInTimeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 签到时间 服务实现类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-07
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CheckInTimeServiceImpl extends ServiceImpl<CheckInTimeMapper, CheckInTime> implements CheckInTimeService {

    /**
     * 查询打卡周期
     *
     * @param id
     * @return
     */
    @Override
    public List<String> queryWeekBySignId(String id) {
        LambdaQueryWrapper<CheckInTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInTime::getSignId, id);
        return this.list(wrapper).stream().map(CheckInTime::getWeek).collect(Collectors.toList());
    }

    /**
     * 删除打卡周期
     *
     * @param id
     */
    @Override
    public void deleteWeekBySignId(String id) {
        LambdaQueryWrapper<CheckInTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInTime::getSignId, id);
        this.remove(wrapper);
    }
}
