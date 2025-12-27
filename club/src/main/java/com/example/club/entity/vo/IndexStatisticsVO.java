package com.example.club.entity.vo;

import lombok.Data;

@Data
public class IndexStatisticsVO {
    private Integer activeClubCount;      // 活跃社团数（状态正常的社团）
    private Integer ongoingActivityCount; // 正在进行的活动数（开始时间 < 现在 < 结束时间）
    private Integer signingActivityCount; // 正在报名的活动数（现在 < 开始时间-1天）
}