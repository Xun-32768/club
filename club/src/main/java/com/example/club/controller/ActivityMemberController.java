package com.example.club.controller;

import com.example.club.common.Result;
import com.example.club.entity.vo.MyActivityVO;
import com.example.club.service.IActivityMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动报名记录表 前端控制器
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/activity/member")
public class ActivityMemberController {

    @Autowired
    private IActivityMemberService activityMemberService;

    @PostMapping("/signup")
    public Result<?> signup(@RequestBody Map<String, Long> params) {
        Long activityId = params.get("activityId");
        if (activityId == null) return Result.fail("活动ID不能为空");

        try {
            activityMemberService.signup(activityId);
            return Result.success("报名成功！");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
    @GetMapping("/my")
    public Result<List<MyActivityVO>> getMyActivities() {
        return Result.success(activityMemberService.getMyActivities());
    }
}

