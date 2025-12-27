package com.example.club.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClubVO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String presidentName; // 增加这个字段
    private Long creatorId;
    private Integer status;
    private LocalDateTime createTime;
    private Integer memberCount;
    private Boolean isJoined;
}