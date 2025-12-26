package com.example.club.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.common.Result;
import com.example.club.entity.Club;
import com.example.club.entity.dto.ClubQueryDTO;
import com.example.club.entity.vo.ClubMemberVO;
import com.example.club.entity.vo.ClubVO;
import com.example.club.entity.vo.MyClubVO;
import com.example.club.service.IClubService;
import com.example.club.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社团主表 前端控制器
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/club")
@Slf4j
public class ClubController {
    @Autowired
    private IClubService clubService;

    // 分页获取社团列表
    @PostMapping("/list") // 建议用 POST 方便传 JSON 对象，GET 也可以
    public Result<Page<ClubVO>> list(@RequestBody ClubQueryDTO queryDTO) {
        log.info("获取社团列表");
        Page<ClubVO> page = clubService.listClubs(queryDTO);
        return Result.success(page);
    }

    // 申请加入社团
    @PostMapping("/join")
    public Result<?> joinClub(@RequestBody Map<String, Long> params) {
        // 前端传 JSON: { "clubId": 123 }
        Long clubId = params.get("clubId");

        log.info("申请加入社团：{}",clubId);
        if (clubId == null) {
            return Result.fail("参数错误");
        }
        try{
            clubService.applyJoin(clubId);
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }
        return Result.success("申请已提交，请等待社长审核");
    }

    @GetMapping("/my")
    public Result<List<MyClubVO>> getMyClubs() {
        List<MyClubVO> list = clubService.getMyClubs();
        return Result.success(list);
    }

    // 获取某社团的成员列表
    @GetMapping("/members/{clubId}")
    public Result<List<ClubMemberVO>> getMembers(@PathVariable Long clubId, @RequestParam(required = false) Integer status) {
        log.info("获取：{}成员列表",clubId);
        return Result.success(clubService.getClubMembers(clubId, status));
    }

    // 审核成员
    @PostMapping("/member/audit")
    public Result<?> auditMember(@RequestBody Map<String, Object> params) {
        // 前端传 { "memberId": 100, "pass": true }
        Long memberId = Long.valueOf(params.get("memberId").toString());
        Boolean pass = (Boolean) params.get("pass");
        log.info("成员审核：{},{}",memberId,pass);
        try {
            clubService.auditMember(memberId, pass);
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }

        return Result.success("操作成功");
    }

    @PostMapping("/add")
    public Result<Club> createClub(@RequestBody Club club) {
        log.info("新增社团");
        // 假设当前登录用户 ID 从权限上下文中获取
        Long currentUserId = UserContext.getUserId();
        club.setCreatorId(currentUserId);

        boolean saved = clubService.saveClubWithAdmin(club);

        if (saved) {
            // 返回包含 String 格式 ID 的对象
            return Result.success(club);
        }
        return Result.fail("创建失败");
    }
}
