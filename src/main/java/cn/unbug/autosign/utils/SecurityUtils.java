package cn.unbug.autosign.utils;

import cn.unbug.autosign.config.Constants;
import cn.unbug.autosign.config.ThreadLocalUtils;
import cn.unbug.autosign.entity.SysUser;

/**
 * @ProjectName: AutoSign
 * @Package: cn.unbug.autosign.utils
 * @ClassName: SecurityUtils
 * @Author: zhangtao
 * @Description: []
 * @Date: 2023/6/10 13:43
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static SysUser loginUser() {
        return ThreadLocalUtils.get(Constants.SYS_USER);
    }
}
