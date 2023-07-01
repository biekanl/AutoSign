package cn.unbug.autosign.controller;


import cn.unbug.autosign.config.controlleradvice.UnifiedReturn;
import cn.unbug.autosign.entity.SignReport;
import cn.unbug.autosign.service.SignReportService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 签到汇报表 前端控制器
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-23
 */
@Slf4j
@RestController
@UnifiedReturn
@AllArgsConstructor
@RequestMapping("/signReport")
public class SignReportController {


    private SignReportService signReportService;


    /**
     * 获取汇报列表
     *
     * @param pageNo
     * @param pageSize
     * @param code
     * @return
     */
    @GetMapping("/queryByPage")
    public IPage<SignReport> queryByPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam("code") String code) {
        return signReportService.queryByPage(pageNo, pageSize, code);
    }


    /**
     * 添加 汇报
     * @param signReport
     */
    @PostMapping("/addSignReport")
    public void addSignReport(@RequestBody @Validated SignReport signReport){
        signReportService.addSignReport(signReport);
    }

    /**
     * 修改 汇报
     * @param signReport
     */
    @PostMapping("/editSignReport")
    public void editSignReport(@RequestBody @Validated SignReport signReport){
        signReportService.editSignReport(signReport);
    }

    /**
     * 删除 汇报
     * @param id
     */
    @GetMapping("/deleteSignReport")
    public void deleteSignReport( String id){
        signReportService.deleteSignReport(id);
    }

}
