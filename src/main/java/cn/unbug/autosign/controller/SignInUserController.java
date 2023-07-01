package cn.unbug.autosign.controller;


import cn.unbug.autosign.config.controlleradvice.UnifiedReturn;
import cn.unbug.autosign.entity.SignHistory;
import cn.unbug.autosign.entity.vo.SignInUserVo;
import cn.unbug.autosign.service.SignHistoryService;
import cn.unbug.autosign.service.SignInUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 签到用户 前端控制器
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
@Slf4j
@UnifiedReturn
@RestController
@AllArgsConstructor
@RequestMapping("/signInUser")
public class SignInUserController {


    private SignInUserService signInUserService;


    private SignHistoryService signHistoryService;

    /**
     * 添加签到客户
     *
     * @param signInUser
     */
    @PostMapping("/addSignInUser")
    public void addSignInUser(@RequestBody @Validated SignInUserVo signInUser) {
        signInUserService.addSignInUser(signInUser);
    }

    /**
     * 获取签到客户列表
     *
     * @param pageNo
     * @param pageSize
     * @param accountNumber
     * @return
     */
    @GetMapping("/queryByPage")
    public IPage<SignInUserVo> queryByPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam("accountNumber") String accountNumber) {
        return signInUserService.queryByPage(pageNo, pageSize, accountNumber);
    }


    /**
     * 修改状态
     *
     * @param
     */
    @GetMapping("/changeStatus")
    public String changeStatus(@RequestParam("id") String id) {
        return signInUserService.changeStatus(id);
    }

    /**
     * 根据id查询
     *
     * @param
     */
    @GetMapping("/selectUserById")
    public SignInUserVo selectUserById(@RequestParam("id") String id) {
        return signInUserService.selectUserById(id);
    }


    /**
     * 更新用户
     */
    @PostMapping("/updateUser")
    public void updateUser(@RequestBody @Validated SignInUserVo signInUser) {
        signInUserService.updateUser(signInUser);
    }

    /**
     * 根据id删除用户
     *
     * @param
     */
    @GetMapping("/deleteUserById")
    public void deleteUserById(@RequestParam("id") String id) {
        signInUserService.deleteUserById(id);
    }

    /**
     * 获取签到记录
     *
     * @param pageNo
     * @param pageSize
     * @param accountNumber
     * @return
     */
    @GetMapping("/queryHistoryByPage")
    public IPage<SignHistory> queryHistoryByPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam("accountNumber") String accountNumber) {
        return signHistoryService.queryHistoryByPage(pageNo, pageSize, accountNumber);
    }
}
