package cn.unbug.autosign.service.impl;

import cn.unbug.autosign.entity.SignHistory;
import cn.unbug.autosign.mapper.SignHistoryMapper;
import cn.unbug.autosign.service.SignHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 签到记录表 服务实现类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SignHistoryServiceImpl extends ServiceImpl<SignHistoryMapper, SignHistory> implements SignHistoryService {

    private SignHistoryMapper signHistoryMapper;

    /**
     * 获取最近一次打卡
     *
     * @param id
     * @return
     */
    @Override
    public SignHistory queryLatestHistory(String id) {
        return signHistoryMapper.queryLatestHistory(id);
    }

    @Override
    public IPage<SignHistory> queryHistoryByPage(Integer pageNo, Integer pageSize, String accountNumber) {
        LambdaQueryWrapper<SignHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignHistory::getSignId,accountNumber).orderByDesc(SignHistory::getUpdatedTime);
        IPage<SignHistory> queryPage = new Page<>(pageNo, pageSize);
        this.page(queryPage, wrapper);
        return queryPage;
    }
}
