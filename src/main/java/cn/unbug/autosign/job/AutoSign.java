package cn.unbug.autosign.job;

import cn.unbug.autosign.config.Constants;
import cn.unbug.autosign.entity.SignInUser;
import cn.unbug.autosign.service.SignInUserService;
import cn.unbug.autosign.utils.AutoSignUtils;
import cn.unbug.autosign.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ProjectName: autosign
 * @Package: cn.unbug.autosign.job
 * @ClassName: AotuSign
 * @Author: Administrator
 * @Description: []
 * @Date: 2023/6/6 22:01
 */
@Slf4j
@Component
@AllArgsConstructor
public class AutoSign {


    private SignInUserService signInUserService;


    /**
     * 定时任务 打卡 将数据放入redis
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void signJob() {
        List<SignInUser> signInUsers = signInUserService.inquireAboutCustomersToPunchIn(null);
        log.info("=== AutoSign sign signInUsers :{}  ===", signInUsers.size());
        signInUserService.sign(signInUsers);
    }

    /**
     * 定时任务 日报
     */
    @Scheduled(cron = "0 0 19 * * ?")
    public void dailyReportJob() {
        List<SignInUser> signInUsers = signInUserService.dailyReportJob();
        log.info("=== AutoSign sign dailyReportJob :{}  ===", signInUsers.size());
        if (CollectionUtils.isNotEmpty(signInUsers)) {
            for (SignInUser signInUser : signInUsers) {
                try {
                    AutoSignUtils.sendReport(signInUser, "0");
                } catch (Exception e) {
                    log.error("=== AutoSign dailyReportJob exception ===", e);
                    if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                        MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, "日报推送异常！");
                    }
                }
            }
        }
    }

    /**
     * 定时任务 周报报
     */
    @Scheduled(cron = "0 0 19 ? * 6")
    public void weeklyReportJob() {
        List<SignInUser> signInUsers = signInUserService.weeklyReportJob();
        log.info("=== AutoSign sign weeklyReportJob :{}  ===", signInUsers.size());
        if (CollectionUtils.isNotEmpty(signInUsers)) {
            for (SignInUser signInUser : signInUsers) {
                try {
                    AutoSignUtils.sendReport(signInUser, "1");
                } catch (Exception e) {
                    log.error("=== AutoSign weeklyReportJob exception ===", e);
                    if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                        MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, "周报推送异常！");
                    }
                }
            }
        }
    }


    /**
     * 定时任务 周报报
     */
    @Scheduled(cron = "0 0 19 L * ?")
    public void monthlyReportJob() {
        List<SignInUser> signInUsers = signInUserService.monthlyReportJob();
        log.info("=== AutoSign sign monthlyReportJob :{}  ===", signInUsers.size());
        if (CollectionUtils.isNotEmpty(signInUsers)) {
            for (SignInUser signInUser : signInUsers) {
                try {
                    AutoSignUtils.sendReport(signInUser, "2");
                } catch (Exception e) {
                    log.error("=== AutoSign monthlyReportJob exception ===", e);
                    if (StringUtils.isNotBlank(signInUser.getPushToken())) {
                        MessageUtils.sendMsg(signInUser.getPushToken(), Constants.REPORT_NOTIFICATIONS, "月报推送异常！");
                    }
                }
            }
        }
    }

}
