package com.example.club.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivityMember;
import com.example.club.entity.Club;
import com.example.club.entity.vo.ActivityVO;
import com.example.club.entity.vo.IndexStatisticsVO;
import com.example.club.mapper.ActivityMapper;
import com.example.club.mapper.ActivityMemberMapper;
import com.example.club.mapper.ClubMapper;
import com.example.club.service.IActivityMemberService;
import com.example.club.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private ClubServiceImpl clubService;

    @Override
    public void createActivity(Activity activity) {
        // 设置初始状态为已发布
        activity.setStatus(1);
        this.save(activity);
    }

    @Override
    public Page<ActivityVO> listActivitiesWithStatus(int pageNum, int size, String title) {
        Long currentUserId = UserContext.getUserId();

        // 分页查询基础活动信息
        Page<Activity> page = new Page<>(pageNum, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 1); // 只显示已发布的
        if (StrUtil.isNotBlank(title)) wrapper.like(Activity::getTitle, title);
        wrapper.orderByDesc(Activity::getStartTime);
        this.page(page, wrapper);

        // 获取当前页的所有活动ID
        List<Long> activityIds = page.getRecords().stream().map(Activity::getId).toList();

        // 查询当前用户报过名且在当前页中的活动ID集合
        Set<Long> signedUpIds = new HashSet<>();
        if (!activityIds.isEmpty() && currentUserId != null) {
            signedUpIds = activityMemberMapper.selectList(new LambdaQueryWrapper<ActivityMember>()
                            .eq(ActivityMember::getUserId, currentUserId)
                            .in(ActivityMember::getActivityId, activityIds))
                    .stream().map(ActivityMember::getActivityId).collect(Collectors.toSet());
        }

        // 转换为 VO 并设置状态
        final Set<Long> finalSignedUpIds = signedUpIds;
        List<ActivityVO> voList = page.getRecords().stream().map(a -> {
            ActivityVO vo = new ActivityVO();
            BeanUtil.copyProperties(a, vo);
            vo.setIsSignedUp(finalSignedUpIds.contains(a.getId())); // 设置是否已报名
            return vo;
        }).toList();

        Page<ActivityVO> resultPage = new Page<>();
        BeanUtil.copyProperties(page, resultPage, "records");
        resultPage.setRecords(voList);
        return resultPage;
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

    @Override
    public IndexStatisticsVO getIndexStatistics() {
        LocalDateTime now = LocalDateTime.now();
        IndexStatisticsVO vo = new IndexStatisticsVO();

        // 1. 活跃社团数量 (状态为1的社团)
        vo.setActiveClubCount((int) clubService.count(new LambdaQueryWrapper<Club>().eq(Club::getStatus, 1)));

        // 2. 正在进行的活动数量 (状态为1 且 当前时间在开始和结束时间之间)
        vo.setOngoingActivityCount((int) this.count(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getStatus, 1)
                .le(Activity::getStartTime, now)
                .ge(Activity::getEndTime, now)));

        // 3. 正在报名的活动数量 (状态为1 且 当前时间在开始时间1天之前)
        LocalDateTime signupDeadline = now.plusDays(1);
        vo.setSigningActivityCount((int) this.count(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getStatus, 1)
                .gt(Activity::getStartTime, signupDeadline)));

        return vo;
    }





}
