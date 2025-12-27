package com.example.club.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivityMember;
import com.example.club.mapper.ActivityMapper;
import com.example.club.mapper.ActivityMemberMapper;
import com.example.club.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    @Autowired
    private ActivityMemberMapper activityMemberMapper;

    @Override
    public void createActivity(Activity activity) {
        // 设置初始状态为已发布
        activity.setStatus(1);
        this.save(activity);
    }

    @Override
    public Page<Activity> listActivities(int pageNum, int size, String title) {
        Page<Activity> page = new Page<>(pageNum, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        // 只展示已发布的活动
        wrapper.eq(Activity::getStatus, 1);

        // 搜索过滤
        if (StrUtil.isNotBlank(title)) {
            wrapper.like(Activity::getTitle, title);
        }

        // 按开始时间倒序排列
        wrapper.orderByDesc(Activity::getStartTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<Activity> getActivitiesByClubId(Long clubId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getClubId, clubId)
                .orderByDesc(Activity::getStartTime); // 按开始时间倒序
        return this.list(wrapper);
    }

    @Override
    public Activity checkActivityForSignup(Long activityId) {
        Activity activity = this.getById(activityId);
        if (activity == null) throw new RuntimeException("活动不存在");
        if (activity.getStatus() != 1) throw new RuntimeException("活动当前不在发布状态");

        // 检查人数限制
        if (activity.getMaxPeople() > 0) {
            Long currentCount = activityMemberMapper.selectCount(
                    new LambdaQueryWrapper<ActivityMember>().eq(ActivityMember::getActivityId, activityId)
            );
            if (currentCount >= activity.getMaxPeople()) {
                throw new RuntimeException("活动名额已满");
            }
        }
        return activity;
    }
}
