package com.example.club.service;

import com.example.club.entity.ActivityMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.entity.User;
import com.example.club.entity.vo.ClubMemberVO;
import com.example.club.entity.vo.MyActivityVO;

import java.util.List;

/**
 * <p>
 * 活动报名记录表 服务类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
public interface IActivityMemberService extends IService<ActivityMember> {
    void signup(Long activityId);

    List<MyActivityVO> getMyActivities();

    void cancelSignup(Long activityId);

    // 获取指定活动的报名成员列表
    List<ClubMemberVO> getActivityMembers(Long activityId);

    // 签到操作
    void checkin(Long recordId);

    // 移除报名记录（由管理员/社长操作）
    void removeSignup(Long recordId);
}
