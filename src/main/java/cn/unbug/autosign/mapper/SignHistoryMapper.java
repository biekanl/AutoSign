package cn.unbug.autosign.mapper;

import cn.unbug.autosign.entity.SignHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 签到记录表 Mapper 接口
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
@Mapper
public interface SignHistoryMapper extends BaseMapper<SignHistory> {

    /**
     * 获取最近一次打卡
     *
     * @param id
     * @return
     */
    SignHistory queryLatestHistory(String id);
}
