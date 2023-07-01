package cn.unbug.autosign.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 签到汇报表
 * </p>
 *
 * @author zhangtao
 * @since 2023-06-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SignReport implements Serializable{

    /**
    * serialVersionUID
    */
    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private String id;

    /**
     * 汇报类型（0 日报 1 周报 2月报）
     */
    private String reportCode;

    /**
     * 实习记录
     */
    private String record;

    /**
     * 实习总结
     */
    private String summary;

    /**
     * 登记人
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
