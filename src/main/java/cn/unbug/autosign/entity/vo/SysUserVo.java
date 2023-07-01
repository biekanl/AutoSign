package cn.unbug.autosign.entity.vo;

import cn.unbug.autosign.entity.SysUser;
import lombok.Data;

/**
 * @ProjectName: AutoSign
 * @Package: cn.unbug.autosign.entity
 * @ClassName: SysUserVo
 * @Author: zhangtao
 * @Description: []
 * @Date: 2023/6/10 15:16
 */
@Data
public class SysUserVo extends SysUser {

    /**
     * 代理数量
     */
    private Integer proxys;


    /**
     * 客户数量
     */
    private Integer customer;
}
