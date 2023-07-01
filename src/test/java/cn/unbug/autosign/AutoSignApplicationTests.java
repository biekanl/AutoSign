package cn.unbug.autosign;

import cn.unbug.autosign.entity.SignInUser;
import cn.unbug.autosign.service.SignInUserService;
import cn.unbug.autosign.utils.AutoSignUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@SpringBootTest
class AutoSignApplicationTests {

    @Autowired
    private SignInUserService signInUserService;

    @Test
    void contextLoads() throws NoSuchAlgorithmException, InterruptedException {
        SignInUser byId = signInUserService.getById("1667766892596228097");
        log.info("=== AutoSignApplicationTests contextLoads {} ===", JSON.toJSONString(byId));
        AutoSignUtils.sendReport(byId,"0");
    }

    @Test
    void contextLoads1() throws NoSuchAlgorithmException {
        List<SignInUser> signInUsers = signInUserService.dailyReportJob();
        log.info("=== AutoSignApplicationTests contextLoads {} ===", JSON.toJSONString(signInUsers));
    }

}
