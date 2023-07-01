package cn.unbug.autosign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Indexed;

/**
 * spring boot 启动类
 */
@Indexed
@EnableScheduling
@ServletComponentScan
@SpringBootApplication
public class AutoSignApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoSignApplication.class, args);
    }

}
