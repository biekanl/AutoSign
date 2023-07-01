package cn.unbug.autosign.config.redisson;


import cn.unbug.autosign.config.exception.ErrorCodeEnum;
import cn.unbug.autosign.config.exception.ServiceException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * @author zhangtao
 */
@Slf4j
@Aspect
@Component
public class DistributedLockAspect {

    private ExpressionParser parser = new SpelExpressionParser();

    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 环绕处理
     */
    @Around(value = "@annotation(distributedLock)")
    public Object doAround(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        String lockKey = null;
        try {
            Method method = this.getMethod(joinPoint);
            //获取方法的参数值
            Object[] args = joinPoint.getArgs();
            EvaluationContext context = this.bindParam(method, args);
            //是否是以#开头
            if (StringUtils.startsWith(distributedLock.lockKey(), "#")) {
                //根据spel表达式获取值
                Expression expression = parser.parseExpression(distributedLock.lockKey());
                Object key = expression.getValue(context);
                log.info("=== DistributedLockAspect doAround key :{}===", JSON.toJSONString(key));
                //key
                lockKey = distributedLock.prefix() + key;
            } else {
                //key
                lockKey = distributedLock.prefix() + distributedLock.lockKey();
            }
            RedisLockUtil.lock(lockKey, distributedLock.lockTime(), distributedLock.timeUnit());
            long start = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info("=== DistributedLockAspect doAround use time [{}] ms ===", end - start);
            return proceed;
        } catch (Throwable throwable) {
            log.error("环绕拦截错误！");
            throw new ServiceException(ErrorCodeEnum.SERVER_BUSY);
        } finally {
            RedisLockUtil.unlock(lockKey);
        }
    }


    /**
     * 获取当前执行的方法
     *
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Method targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        return targetMethod;
    }

    /**
     * 将方法的参数名和参数值绑定
     *
     * @param method 方法，根据方法获取参数名
     * @param args   方法的参数值
     * @return
     */
    private EvaluationContext bindParam(Method method, Object[] args) {
        //获取方法的参数名
        String[] params = discoverer.getParameterNames(method);
        //将参数名与参数值对应起来
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        return context;
    }
}
