package cn.unbug.autosign.config.exception;


import java.util.Arrays;
import java.util.List;

/**
 * 业务静态常量
 *
 * @author zhangtao
 */
public class BizConstant {
    //参数为空
    public static final String PARAMETER_IS_EMPTY = "参数为空！";
    //无对应用户
    public static final String NO_CORRESPONDING_USER = "无对应用户！";
    //超级管理员
    public static final String SUPER_ADMINISTRATOR = "zhangsan";
    //不可禁止
    public static final String NOT_FORBIDDEN = "超级管理员不可锁定！";
    //不可禁止自己
    public static final String DONT_BAN_YOURSELF = "不可锁定自己！";
    //操作成功
    public static final String SUCCESSFUL_OPERATION = "操作成功！";
    //保存成功
    public static final String SAVED_SUCCESSFULLY = "保存成功！";
    //删除成功
    public static final String DETELED_SUCCESSFULLY = "删除成功！";
    //认证缓存key
    public static final String AUTHENTICATION_CACHE = "shiro:cache:AuthenticationCache:";
    //授权缓存key
    public static final String AUTHORIZATION_CACHE = "shiro:cache:AuthorizationCache:";
    //缓存toke key
    public static final String CACHE_TOKEN = ":token";
    //缓存user key
    public static final String CACHE_USER = ":user";
    //过期时间6小时
    public static final long EXPIRE_TIME = 6 * 60 * 60 * 1000;
    //默认密码
    public static final String DEFAULT_PASSWORD = "123456";
    //男
    public static final String MAN = "MAN";
    //女
    public static final String WOMAN = "WOMAN";
    //字符
    public static final String VARCHAR = "QWERTYUIOPASDFGHJKLZXCVBNM123456789";
    //不记录日志的方法
    public static final List<String> UN_LOG = Arrays.asList("logout", "userListDownload", "batchUploadSysLabour");
}
