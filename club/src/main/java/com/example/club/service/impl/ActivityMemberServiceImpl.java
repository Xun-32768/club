package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivityMember;
import com.example.club.entity.vo.MyActivityVO;
import com.example.club.mapper.ActivityMemberMapper;
import com.example.club.service.IActivityMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.service.IActivityService;
import com.example.club.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动报名记录表 服务实现类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Service
public class ActivityMemberServiceImpl extends ServiceImpl<ActivityMemberMapper, ActivityMember> implements IActivityMemberService {

    // ActivityMemberServiceImpl.java
    @Autowired
    private IActivityService activityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signup(Long activityId) {
        Long userId = UserContext.getUserId();

        // 1. 基础校验（活动状态、人数）
        activityService.checkActivityForSignup(activityId);

        // 2. 重复报名校验
        Long count = this.count(new LambdaQueryWrapper<ActivityMember>()
                .eq(ActivityMember::getActivityId, activityId)
                .eq(ActivityMember::getUserId, userId));
        if (count > 0) throw new RuntimeException("您已报名该活动，请勿重复操作");

        // 3. 插入记录
        ActivityMember record = new ActivityMember();
        record.setActivityId(activityId);
        record.setUserId(userId);
        record.setCheckinStatus(0); // 默认未签到
        this.save(record);
    }

    // IActivityMemberService.java 增加接口

    // ActivityMemberServiceImpl.java 实现
    @Override
    public List<MyActivityVO> getMyActivities() {
        Long userId = UserContext.getUserId();

        // 1. 查询我的所有报名记录
        List<ActivityMember> members = this.list(new LambdaQueryWrapper<ActivityMember>()
                .eq(ActivityMember::getUserId, userId)
                .orderByDesc(ActivityMember::getSignupTime));

        if (members.isEmpty()) return new ArrayList<>();

        // 2. 获取活动 ID 集合并查询详情
        List<Long> activityIds = members.stream().map(ActivityMember::getActivityId).toList();
        Map<Long, Activity> activityMap = activityService.listByIds(activityIds).stream()
                .collect(Collectors.toMap(Activity::getId, a -> a));

        // 3. 组装 VO
        return members.stream().map(m -> {
            MyActivityVO vo = new MyActivityVO();
            Activity a = activityMap.get(m.getActivityId());
            if (a != null) {
                vo.setActivityId(a.getId());
                vo.setTitle(a.getTitle());
                vo.setLocation(a.getLocation());
                vo.setStartTime(a.getStartTime());
                vo.setStatus(a.getStatus());
            }
            vo.setCheckinStatus(m.getCheckinStatus());
            vo.setSignupTime(m.getSignupTime());
            return vo;
        }).toList();
    }

}
