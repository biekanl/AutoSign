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
 * 签到记录表
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SignHistory implements Serializable {

    /**
     * serialVersionUID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 签到用户id
     */
    private String signId;

    /**
     * 签到状态（0 签到成功 1 签到失败）
     */
    private String checkInStatus;

    /**
     * 签到内容
     */
    private String checkInContent;

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
