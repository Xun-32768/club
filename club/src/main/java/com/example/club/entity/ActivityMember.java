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
 * 活动报名记录表
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Data
@TableName("activity_member")
public class ActivityMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 报名时间
     */
    @TableField("signup_time")
    private LocalDateTime signupTime;

    /**
     * 签到状态: 0-未签到, 1-已签到
     */
    @TableField("checkin_status")
    private Integer checkinStatus;

    /**
     * 签到时间
     */
    @TableField("checkin_time")
    private LocalDateTime checkinTime;
}
