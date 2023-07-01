package cn.unbug.autosign.config.controlleradvice;

import java.lang.annotation.*;

/**
 * @PackageName: cn.unbug.autosign.controllerAdvice
 * @ClassName: UnifiedReturn
 * @Description: [是否需要统一返回]
 * @Author: zhangtao
 * @Date: 2022/10/07 10:46:15
 * @Version: V1.0
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UnifiedReturn {

    /**
     * 是否统一返回
     *
     * @return
     */
    boolean isItUnified() default true;
}
