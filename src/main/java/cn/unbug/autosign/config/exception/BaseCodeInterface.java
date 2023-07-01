package cn.unbug.autosign.config.exception;

/**
 * 自定义的错误描述枚举类需实现该接口
 *
 * @Author zhangtao
 * @Date 2020/3/1 14:49
 * @Version 1.0
 **/
public interface BaseCodeInterface {

    /**
     * 错误码
     */
    int getResultCode();

    /**
     * 错误描述
     */
    String getResultMsg();

}
