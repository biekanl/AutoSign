package cn.unbug.autosign.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统用户
 * </p>
 *
 * @author zhangtao
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysUser implements Serializable{

    /**
    * serialVersionUID
    */
    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 手机
     */
    private String phone;

    /**
     * 推送token
     */
    private String pushToken;

    /**
     * 剩余次数
     */
    private Integer numberOfConsumptions;

    /**
     * 上级代理
     */
    private String superior;

    /**
     * 是否存在代理权限
     */
    private Integer proxy;

    /**
     * 状态 （0 正常 1 锁定）
     */
    private String status;

    /**
     * 乐观锁
     */
    private Integer revision;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;


}
