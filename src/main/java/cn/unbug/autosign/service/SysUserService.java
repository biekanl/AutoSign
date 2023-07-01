package cn.unbug.autosign.service;

import cn.unbug.autosign.entity.SysUser;
import cn.unbug.autosign.entity.vo.SysUserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author zhangtao
 * @since 2023-05-30
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取代理列表
     *
     * @param pageNo
     * @param pageSize
     * @param phone
     * @return
     */
    IPage<SysUser> queryByPage(Integer pageNo, Integer pageSize, String phone);

    /**
     * 登录
     *
     * @param sysUser
     * @return
     */
    String login(SysUser sysUser);

    /**
     * 获取用户信息
     *
     * @return
     */
    SysUserVo getUserInfo();

    /**
     * 退出
     */
    void loginout();

    /**
     * 添加代理
     *
     * @param sysUser
     */
    void addUser(SysUser sysUser);

    /**
     * 修改密码
     */
    void changePassword(String passWord);

    /**
     * 修改在状态
     *
     * @param
     */
    String changeStatus(String id);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    SysUser selectUserById(String id);

    /**
     * 更新用户
     *
     * @param sysUser
     */
    void updateUser(SysUser sysUser);

    /**
     * 根据id删除用户
     *
     * @param id
     */
    void deleteUserById(String id);
}
