package cn.unbug.autosign.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 签到用户
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SignInUser implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 账号
     */
    @NotBlank(message = "账号不可为空！")
    private String accountNumber;

    /**
     * 密码
     */
    @NotBlank(message = "密码不可为空！")
    private String passWord;

    /**
     * 设备型号
     */
    @NotBlank(message = "设备型号不可为空！")
    private String deviceType;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 经度
     */
    @NotBlank(message = "经度不可为空！")
    private String longitude;

    /**
     * 纬度
     */
    @NotBlank(message = "纬度不可为空！")
    private String latitude;

    /**
     * 推送token
     */
    private String pushToken;

    /**
     * 微信推送
     */
    private Integer wxPush;

    /**
     * 地址
     */
    @NotBlank(message = "地址不可为空！")
    private String address;

    /**
     * 打卡时间
     */
    @NotBlank(message = "打卡时间不可为空！")
    private String clockInTime;

    /**
     * 打卡周期
     */
    @TableField(exist = false)
    private String punchCycle;

    /**
     * 日报
     */
    private String daily;

    /**
     * 周报
     */
    private String weekly;

    /**
     * 月报
     */
    private String monthly;

    /**
     * 到期时间
     */
    @NotBlank(message = "到期时间不可为空！")
    private String expirationTime;

    /**
     * 状态（0 关闭 1 开启）
     */
    private String status;

    /**
     * 代理
     */
    private String superior;


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
