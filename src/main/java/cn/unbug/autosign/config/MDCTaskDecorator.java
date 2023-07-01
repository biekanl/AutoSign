package cn.unbug.autosign.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ProjectName: informationsystem
 * @Package: cn.unbug.autosign.config
 * @ClassName: MDCTaskDecorator
 * @Author: zhangtao
 * @Description: []
 * @Date: 2022/10/17 20:36
 */
@Slf4j
@Component
public class MDCTaskDecorator implements TaskDecorator, BeanPostProcessor {


    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ThreadPoolTaskExecutor) {
            log.info("=== MDCTaskDecorator postProcessBeforeInitialization  {} ===", beanName);
            ((ThreadPoolTaskExecutor) bean).setTaskDecorator(this);
        }
        return bean;
    }
}
