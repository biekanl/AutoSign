package cn.unbug.autosign.controller;


import cn.unbug.autosign.config.controlleradvice.UnifiedReturn;
import cn.unbug.autosign.entity.SysUser;
import cn.unbug.autosign.entity.vo.SysUserVo;
import cn.unbug.autosign.service.SysUserService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统用户 前端控制器
 * </p>
 *
 * @author zhangtao
 * @since 2023-05-30
 */
@Slf4j
@UnifiedReturn
@RestController
@AllArgsConstructor
@RequestMapping("/sysUser")
public class SysUserController {


    private SysUserService sysUserService;


    /**
     * 获取代理列表
     *
     * @param pageNo
     * @param pageSize
     * @param phone
     * @return
     */
    @GetMapping("/queryByPage")
    public IPage<SysUser> queryByPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam("phone") String phone) {
        IPage<SysUser> sysUserIPage = sysUserService.queryByPage(pageNo, pageSize, phone);
        log.info("=== SysUserController queryByPage sysUserIPage :{} ===", JSON.toJSONString(sysUserIPage));
        return sysUserIPage;
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public SysUserVo getUserInfo() {
        return sysUserService.getUserInfo();
    }

    /**
     * 登录
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestBody SysUser sysUser) {
        log.info("=== SysUserController login :{}  ===", JSON.toJSONString(sysUser));
        return sysUserService.login(sysUser);
    }


    /**
     * 退出
     */
    @PostMapping("/loginout")
    public void loginout() {
        sysUserService.loginout();
    }

    /**
     * 添加代理
     *
     * @param sysUser
     */
    @PostMapping("/addUser")
    public void addUser(@RequestBody @Validated SysUser sysUser) {
        log.info("=== SysUserController addUser sysUser :{} ===", JSON.toJSONString(sysUser));
        sysUserService.addUser(sysUser);
    }

    /**
     * 修改密码
     *
     * @param
     */
    @GetMapping("/changePassword")
    public void changePassword(@RequestParam("passWord") String passWord) {
        sysUserService.changePassword(passWord);
    }


    /**
     * 修改在状态
     *
     * @param
     */
    @GetMapping("/changeStatus")
    public String changeStatus(@RequestParam("id") String id) {
        return sysUserService.changeStatus(id);
    }

    /**
     * 根据id查询
     *
     * @param
     */
    @GetMapping("/selectUserById")
    public SysUser selectUserById(@RequestParam("id") String id) {
        return sysUserService.selectUserById(id);
    }


    /**
     * 更新用户
     */
    @PostMapping("/updateUser")
    public void updateUser(@RequestBody @Validated SysUser sysUser) {
        sysUserService.updateUser(sysUser);
    }

    /**
     * 根据id删除用户
     *
     * @param
     */
    @GetMapping("/deleteUserById")
    public void deleteUserById(@RequestParam("id") String id) {
        sysUserService.deleteUserById(id);
    }

}
