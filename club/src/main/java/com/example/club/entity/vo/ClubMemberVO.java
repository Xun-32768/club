package com.example.club.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClubMemberVO {
    private Long id;          // 成员记录ID (并不是用户ID)
    private Long userId;      // 用户ID
    private String realName;  // 真实姓名
    private String username;  // 学号
    private Integer memberRole; // 1-成员, 2-社长
    private Integer status;     // 0-申请中, 1-正式, 2-拒绝
    private LocalDateTime joinTime;
}