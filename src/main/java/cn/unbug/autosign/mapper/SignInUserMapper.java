package cn.unbug.autosign.mapper;

import cn.unbug.autosign.entity.SignInUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 签到用户 Mapper 接口
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
@Mapper
public interface SignInUserMapper extends BaseMapper<SignInUser> {

    /**
     *  根据账号查询客户
     * @param accountNumber
     * @return
     */
    SignInUser selectUserByAccountNumber(String accountNumber);

    /**
     * 查询待签约的客户
     * @return
     */
    List<SignInUser> inquireAboutCustomersToPunchIn(String id);

    /**
     * 获取汇报用户
     * @return
     */
    List<SignInUser> queryReportJobByCode(String type);
}
