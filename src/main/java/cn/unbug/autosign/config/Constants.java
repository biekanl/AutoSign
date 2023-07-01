package cn.unbug.autosign.config;

import cn.hutool.crypto.digest.MD5;

/**
 * @ProjectName: AutoSign
 * @Package: cn.unbug.autosign.config
 * @ClassName: Constants
 * @Author: zhangtao
 * @Description: []
 * @Date: 2023/5/30 21:22
 */
public class Constants {

    /**
     * 用户
     */
    public static final String SYS_USER = "sys_user";


    /**
     * 用户组
     */
    public static final String SYS_USER_GROUP = "sys_user:";


    /**
     * 签到用户组
     */
    public static final String SIGN_USER_GROUP = "sign_user:";


    /**
     * token
     */
    public static final String SYS_TOKEN = "Authorization";

    /**
     * 登录地址
     */
    public static final String SYS_LOGIN_URL = "/sysUser/login";

    /**
     * 过期时间
     */
    public static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    /**
     * 盐
     */
    public static final String SALT = "hello World";

    /**
     * 正常
     */
    public static final String STATUS_0 = "0";

    /**
     * 锁定
     */
    public static final String STATUS_1 = "1";

    /**
     * 通知
     */
    public static final String NOTIFICATIONS = "职校家园打卡通知";

    /**
     * 通知
     */
    public static final String REPORT_NOTIFICATIONS = "职校家园汇报通知";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = MD5.create().digestHex("123456" + SALT);


    /**
     * 消息url
     */
    public static final String MESSAGE_URL = "http://www.pushplus.plus/send?token=%s&title=%s&content=%s&template=html";


}
