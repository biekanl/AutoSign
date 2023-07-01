package cn.unbug.autosign.service;

import cn.unbug.autosign.entity.SignInUser;
import cn.unbug.autosign.entity.vo.SignInUserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 签到用户 服务类
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
public interface SignInUserService extends IService<SignInUser> {

    /**
     * 添加打卡用户
     *
     * @param signInUser
     */
    void addSignInUser(SignInUserVo signInUser);

    /**
     * 分页查询客户
     * @param pageNo
     * @param pageSize
     * @param accountNumber
     * @return
     */
    IPage<SignInUserVo> queryByPage(Integer pageNo, Integer pageSize, String accountNumber);

    /**
     * 修改状态
     *
     * @param
     */
    String changeStatus(String id);

    /**
     * 根据id查询
     *
     * @param
     */
    SignInUserVo selectUserById(String id);


    /**
     * 更新用户
     */
    void updateUser(SignInUserVo signInUser);

    /**
     * 根据id删除用户
     *
     * @param
     */
    void deleteUserById(String id);

    /**
     * 查询待签约的客户
     * @return
     */
    List<SignInUser> inquireAboutCustomersToPunchIn(String id);

    /**
     * 设置打卡
     * @param signInUsers
     */
    void sign(List<SignInUser> signInUsers);

    /**
     * 获取日报用户
     * @return
     */
    List<SignInUser> dailyReportJob();

    /**
     * 获取周报用户
     * @return
     */
    List<SignInUser> weeklyReportJob();

    /**
     * 获取月报用户
     * @return
     */
    List<SignInUser> monthlyReportJob();
}
