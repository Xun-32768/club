package com.example.club.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MyActivityVO {
    private Long activityId;
    private String title;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;        // 活动状态: 0-草稿, 1-已发布, 2-已结束
    private Integer checkinStatus; // 签到状态: 0-未签到, 1-已签到
    private LocalDateTime signupTime;
}