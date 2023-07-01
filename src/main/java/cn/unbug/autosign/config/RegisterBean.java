package cn.unbug.autosign.config;


import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @PackageName: cn.unbug.autosign.config
 * @ClassName: RegisterBean
 * @Description: []
 * @Author: zhangtao
 * @Date: 2022/10/07 11:33:58
 * @Version: V1.0
 **/
@Component
public class RegisterBean {

    /**
     * 线程池
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(5);
        //最大线程数  [Runtime.getRuntime().availableProcessors() 获取服务器核心数（容器中不可以使用）]
        threadPoolTaskExecutor.setMaxPoolSize(10);
        //线程存活时间
        threadPoolTaskExecutor.setKeepAliveSeconds(3600);
        //队列大小
        threadPoolTaskExecutor.setQueueCapacity(10000);
        //拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //线程前缀
        threadPoolTaskExecutor.setThreadNamePrefix("auto-thread-");
        return threadPoolTaskExecutor;
    }

    /**
     * redis 处理
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // redis 连接工厂
        container.setConnectionFactory(connectionFactory);
        return container;
    }

}
