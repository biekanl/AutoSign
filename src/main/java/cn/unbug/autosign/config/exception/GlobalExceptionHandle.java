package cn.unbug.autosign.config.exception;

import cn.unbug.autosign.config.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author zhangtao
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(ServiceException.class)
    public AjaxResult serviceException(ServiceException exception) {
        log.error("=== GlobalExceptionHandle serviceException ===", exception);
        return AjaxResult.error(exception.getErrorCode(), exception.getErrorMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.info("=== GlobalExceptionHandle methodArgumentNotValidException===", exception);
        BindingResult result = exception.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        if (result.hasErrors()) {
            result.getAllErrors().forEach(p -> {
                FieldError fieldError = (FieldError) p;
                errorMsg.append(fieldError.getDefaultMessage());
            });
        }
        return AjaxResult.error(errorMsg.toString());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult exception(Exception e) {
        log.error("=== GlobalExceptionHandle exception===", e);
        return AjaxResult.error(ErrorCodeEnum.SERVER_BUSY.getResultMsg());
    }

    @ExceptionHandler(Throwable.class)
    public AjaxResult throwable(Throwable throwable) {
        log.error("=== GlobalExceptionHandle throwable ===", throwable);
        return AjaxResult.error(ErrorCodeEnum.SERVER_BUSY.getResultMsg());
    }
}
