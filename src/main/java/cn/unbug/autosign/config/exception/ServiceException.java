package cn.unbug.autosign.config.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 *
 * @Author zhangtao
 * @Date 2020/3/1 14:47
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected int errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
        this.errorCode = 500;
        this.errorMsg = message;
    }

    public ServiceException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }


    public ServiceException(BaseCodeInterface errorInfoInterface) {
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }

    public ServiceException(BaseCodeInterface errorInfoInterface, String message) {
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = message;
    }


}
