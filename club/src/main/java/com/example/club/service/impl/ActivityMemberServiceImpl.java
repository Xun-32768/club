package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivityMember;
import com.example.club.entity.User;
import com.example.club.entity.vo.ClubMemberVO;
import com.example.club.entity.vo.MyActivityVO;
import com.example.club.mapper.ActivityMemberMapper;
import com.example.club.mapper.UserMapper;
import com.example.club.service.IActivityMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.service.IActivityService;
import com.example.club.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private IActivityService activityService;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signup(Long activityId) {
        Long userId = UserContext.getUserId();

        // 1. 基础校验（活动状态、人数）
        activityService.checkActivityForSignup(activityId);

//        时间校验
        Activity activity = activityService.checkActivityForSignup(activityId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = activity.getStartTime().minusDays(1); // 开始时间减去1天

        if (now.isAfter(deadline)) {
            throw new RuntimeException("报名已截止（活动开始前24小时停止报名）");
        }

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
                vo.setEndTime(a.getEndTime());
                vo.setStatus(a.getStatus());
            }
            vo.setCheckinStatus(m.getCheckinStatus());
            vo.setSignupTime(m.getSignupTime());
            return vo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSignup(Long activityId) {
        Long userId = UserContext.getUserId(); // 获取当前登录用户

        // 1. 校验活动是否已开始（可选安全校验）
        Activity activity = activityService.getById(activityId);
        if (activity != null && activity.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("活动已开始，无法取消报名");
        }

        // 2. 删除报名记录
        LambdaQueryWrapper<ActivityMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityMember::getActivityId, activityId)
                .eq(ActivityMember::getUserId, userId);

        this.remove(wrapper);
    }

    @Override
    public List<ClubMemberVO> getActivityMembers(Long activityId) {
        // 1. 查询报名记录
        List<ActivityMember> members = this.list(new LambdaQueryWrapper<ActivityMember>()
                .eq(ActivityMember::getActivityId, activityId)
                .orderByDesc(ActivityMember::getSignupTime));

        if (members.isEmpty()) return new ArrayList<>();

        // 2. 批量查询用户信息并封装到 VO
        List<Long> userIds = members.stream().map(ActivityMember::getUserId).toList();
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return members.stream().map(m -> {
            ClubMemberVO vo = new ClubMemberVO();
            vo.setId(m.getId()); // 记录ID
            vo.setUserId(m.getUserId());
            User u = userMap.get(m.getUserId());
            if (u != null) {
                vo.setRealName(u.getRealName());
                vo.setUsername(u.getUsername());
            }
            vo.setStatus(m.getCheckinStatus()); // 借用status字段表示签到状态
            vo.setJoinTime(m.getSignupTime()); // 借用joinTime表示报名时间
            return vo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkin(Long recordId) {
        // 1. 获取报名记录
        ActivityMember record = this.getById(recordId);
        if (record == null) {
            throw new RuntimeException("报名记录不存在");
        }

        // 2. 校验是否已经签到（防止重复签到）
        if (record.getCheckinStatus() != null && record.getCheckinStatus() == 1) {
            throw new RuntimeException("该成员已签到，请勿重复操作");
        }

        // 3. 获取活动详情以校验时间
        Activity activity = activityService.getById(record.getActivityId());
        if (activity == null) {
            throw new RuntimeException("对应活动不存在");
        }

        // 4. 校验活动是否已经开始
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime())) {
            throw new RuntimeException("活动尚未开始，暂时无法签到");
        }

        // 5. 执行签到更新
        record.setCheckinStatus(1);
        record.setCheckinTime(now);
        this.updateById(record);
    }

    @Override
    public void removeSignup(Long recordId) {
        this.removeById(recordId);
    }


}
