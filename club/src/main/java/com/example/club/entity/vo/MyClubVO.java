package com.example.club.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MyClubVO {
    private Long clubId;
    private String clubName;
    private String clubCategory; // 社团类型
    private String clubLogo;     // Logo
    private Integer memberRole;  // 我的职位: 1-成员, 2-社长
    private Integer joinStatus;  // 我的状态: 0-审核中, 1-已入社, 2-被拒绝
    private LocalDateTime joinTime;
}