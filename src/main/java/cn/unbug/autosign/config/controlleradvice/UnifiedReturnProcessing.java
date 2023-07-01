package cn.unbug.autosign.config.controlleradvice;

import cn.unbug.autosign.config.AjaxResult;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.File;
import java.util.Objects;

/**
 * @PackageName: cn.unbug.autosign.controllerAdvice
 * @ClassName: UnifiedReturnProcessing
 * @Description: [统一返回/统一异常处理]
 * @Author: zhangtao
 * @Date: 2022/10/07 10:38:17
 * @Version: V1.0
 **/
@Slf4j
@RestControllerAdvice
public class UnifiedReturnProcessing implements ResponseBodyAdvice<Object> {


    /**
     * 是否统一返回判断
     *
     * @param methodParameter
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {

        //如果方法上有注解 且统一返回值是true 则返回
        UnifiedReturn methodAnnotation = methodParameter.getMethodAnnotation(UnifiedReturn.class);
        if (Objects.nonNull(methodAnnotation)) {
            return methodAnnotation.isItUnified();
        }

        //如果类上存在统一返回注解 且统一返回值为true 则返回包装
        UnifiedReturn clazzAnnotation = methodParameter.getContainingClass().getAnnotation(UnifiedReturn.class);
        if (Objects.nonNull(clazzAnnotation)) {
            return clazzAnnotation.isItUnified();
        }
        return false;
    }

    /**
     * 统一返回数据
     *
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return AjaxResult.success();
        }
        if (body instanceof AjaxResult || body instanceof File || body instanceof ResponseEntity) {
            return body;
        }
        log.debug("=== UnifiedReturnProcessing  beforeBodyWrite body {} ===", JSON.toJSONString(body));
        return AjaxResult.success(body);
    }
}
