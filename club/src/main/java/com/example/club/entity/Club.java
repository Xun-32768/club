package com.example.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 社团主表
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Data
@TableName("club")
public class Club implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 社团ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 社团名称
     */
    @TableField("name")
    private String name;

    /**
     * 分类(科技/艺术/体育)
     */
    @TableField("category")
    private String category;

    /**
     * 社团介绍
     */
    @TableField("description")
    private String description;

    /**
     * 创建申请人ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 状态: 0-审核中, 1-正常, 2-驳回, 3-已注销
     */
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;
}
