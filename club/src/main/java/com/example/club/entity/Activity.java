package com.example.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 活动表
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Data
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 举办社团ID
     */
    @TableField("club_id")
    private Long clubId;

    /**
     * 活动标题
     */
    @TableField("title")
    private String title;

    /**
     * 活动详情(富文本)
     */
    @TableField("content")
    private String content;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 添加此注解
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 添加此注解
    private LocalDateTime endTime;

    /**
     * 活动地点
     */
    @TableField("location")
    private String location;

    /**
     * 最大人数限制(0不限)
     */
    @TableField("max_people")
    private Integer maxPeople;

    /**
     * 状态: 0-草稿, 1-已发布, 2-已结束
     */
    @TableField("status")
    private Integer status;
}
