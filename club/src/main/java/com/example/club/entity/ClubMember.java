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
 * 社团成员关联表
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Data
@TableName("club_member")
public class ClubMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 社团ID
     */
    @TableField("club_id")
    private Long clubId;

    /**
     * 学生ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 职位: 1-普通成员,  2-社长
     */
    @TableField("member_role")
    private Integer memberRole;

    /**
     * 状态: 0-申请中, 1-已入社, 2-已拒绝
     */
    @TableField("status")
    private Integer status;

    @TableField("join_time")
    private LocalDateTime joinTime;
}
