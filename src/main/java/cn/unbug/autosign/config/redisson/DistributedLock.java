package cn.unbug.autosign.config.redisson;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangtao
 * 注解aop 仅支持注解在方法上（在类上锁的粒度过大）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 分布式锁 key前缀
     */
    String prefix() default "";

    /**
     * 分布式锁 key
     */
    String lockKey();

    /**
     * 分布式锁 默认上锁时间
     */
    int lockTime() default 5;

    /**
     * 分布式锁 默认上锁时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
