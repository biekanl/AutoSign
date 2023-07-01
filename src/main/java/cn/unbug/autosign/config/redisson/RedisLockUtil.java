package cn.unbug.autosign.config.redisson;

import cn.unbug.autosign.config.SpringContextHolder;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;

import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁帮助类
 *
 * @author zhangtao
 */
@Slf4j
class RedisLockUtil {


    private static DistributedLocker distributedLocker = SpringContextHolder.getBean("redisDistributedLocker", DistributedLocker.class);

    /**
     * 加锁
     *
     * @param lockKey
     * @return
     */
    public static RLock lock(String lockKey) {
        log.info("--- RedisLockUtil lock  lockKey :[{}]", lockKey);
        return distributedLocker.lock(lockKey);
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public static void unlock(String lockKey) {
        log.info("--- RedisLockUtil unlock lockKey :[{}]", lockKey);
        distributedLocker.unlock(lockKey);
    }

    /**
     * 释放锁
     *
     * @param lock
     */
    public static void unlock(RLock lock) {
        distributedLocker.unlock(lock);
    }

    /**
     * 带超时的锁
     *
     * @param lockKey
     * @param timeout 超时时间   单位：秒
     */
    public static RLock lock(String lockKey, int timeout) {
        log.info("--- RedisLockUtil RLock lockKey :[{}] timeout :[{}]", lockKey, timeout);
        return distributedLocker.lock(lockKey, timeout);
    }

    /**
     * 带超时的锁
     *
     * @param lockKey
     * @param unit    时间单位
     * @param timeout 超时时间
     */
    public static RLock lock(String lockKey, int timeout, TimeUnit unit) {
        log.info("--- RedisLockUtil lock lockKey :[{}] timeout :[{}] TimeUnit :[{}]", lockKey, timeout, JSON.toJSONString(unit));
        return distributedLocker.lock(lockKey, unit, timeout);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     */
    public static boolean tryLock(String lockKey, int waitTime, int leaseTime) {
        log.info("--- RedisLockUtil tryLock lockKey :[{}] waitTime :[{}] leaseTime :[{}]", lockKey, waitTime, leaseTime);
        return distributedLocker.tryLock(lockKey, TimeUnit.SECONDS, waitTime, leaseTime);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey
     * @param unit      时间单位
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     */
    public static boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        log.info("--- RedisLockUtil tryLock lockKey :[{}] waitTime :[{}] leaseTime :[{}]", lockKey, waitTime, leaseTime);
        return distributedLocker.tryLock(lockKey, unit, waitTime, leaseTime);
    }

    /**
     * 获取计数器
     *
     * @param name
     * @return
     */
    public static RCountDownLatch getCountDownLatch(String name) {
        return distributedLocker.getCountDownLatch(name);
    }

    /**
     * 获取信号量
     *
     * @param name
     * @return
     */
    public static RSemaphore getSemaphore(String name) {
        return distributedLocker.getSemaphore(name);
    }
}
