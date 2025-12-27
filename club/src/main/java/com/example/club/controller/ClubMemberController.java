package com.example.club.controller;

import com.example.club.common.Result;
import com.example.club.service.IClubMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 社团成员关联表 前端控制器
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/club/member")
public class ClubMemberController {

    @Autowired
    private IClubMemberService clubMemberService;

    @PostMapping("/quit")
    public Result<?> quitClub(@RequestBody Map<String, Long> params) {
        Long clubId = params.get("clubId");
        if (clubId == null) return Result.fail("参数错误");

        try {
            clubMemberService.quitClub(clubId);
            return Result.success("已成功退出该社团");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
