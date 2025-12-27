package com.example.club.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.common.Result;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivityMember;
import com.example.club.entity.vo.ActivityVO;
import com.example.club.service.IActivityMemberService;
import com.example.club.service.IActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 前端控制器
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/activity")
@Slf4j
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IActivityMemberService activityMemberService;
//    发布新活动
    @PostMapping("/add")
    public Result<?> addActivity(@RequestBody Activity activity) {
        try {
            log.info("发布新活动: {}", activity.getTitle());
            activityService.createActivity(activity);
            return Result.success("活动发布成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
//    展示活动列表
    @PostMapping("/list")
    public Result<Page<ActivityVO>> listActivities(@RequestBody Map<String, Object> params) {
        int pageNum = params.get("page") == null ? 1 : (int) params.get("page");
        int size = params.get("size") == null ? 10 : (int) params.get("size");
        String title = (String) params.get("title");

        Page<ActivityVO> result = activityService.listActivitiesWithStatus(pageNum, size, title);
        return Result.success(result);
    }
//    当前社团发布的活动
    @GetMapping("/club/{clubId}")
    public Result<List<Activity>> getClubActivities(@PathVariable Long clubId) {
        List<Activity> list = activityService.getActivitiesByClubId(clubId);
        return Result.success(list);
    }

//    获取活动详细信息
    @GetMapping("/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.fail("活动不存在");
        }
        // 如果需要返回当前报名人数，可以在此处额外查询 count 并设置到 VO 中
        return Result.success(activity);
    }




}
