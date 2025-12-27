package com.example.club.service;

import com.example.club.entity.ActivityMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.entity.User;
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

}
