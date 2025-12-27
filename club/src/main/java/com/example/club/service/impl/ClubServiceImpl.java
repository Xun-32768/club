package com.example.club.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.entity.Club;
import com.example.club.entity.ClubMember;
import com.example.club.entity.User;
import com.example.club.entity.dto.ClubQueryDTO;
import com.example.club.entity.vo.ClubMemberVO;
import com.example.club.entity.vo.ClubVO;
import com.example.club.entity.vo.MyClubVO;
import com.example.club.mapper.ClubMapper;
import com.example.club.mapper.ClubMemberMapper;
import com.example.club.service.IClubService;
import com.example.club.service.IUserService;
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
 * 社团主表 服务实现类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Service
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements IClubService {


    @Autowired
    private IUserService userService;

    @Autowired
    private ClubMemberMapper clubMemberMapper;

    public Page<ClubVO> listClubs(ClubQueryDTO dto) {

        // 1. 获取当前用户ID和角色
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        Integer role = user.getSystemRole(); // 0-超级管理员, 1-普通学生

        Page<Club> page = new Page<>(dto.page() == null ? 1 : dto.page(), dto.size() == null ? 10 : dto.size());
        LambdaQueryWrapper<Club> wrapper = new LambdaQueryWrapper<>();

//        只显示审核通过的社团
        wrapper.eq(Club::getStatus, 1).orderByAsc(Club::getStatus);
        wrapper.like(StrUtil.isNotBlank(dto.name()), Club::getName, dto.name());
        wrapper.orderByDesc(Club::getCreateTime);

        this.page(page, wrapper);

 // 类型转换 Entity -> VO
        List<Club> records = page.getRecords();
        if (records.isEmpty()) {
            return new Page<>();
        }

        //  提取所有的社长ID (creator_id)
        List<Long> userIds = records.stream().map(Club::getCreatorId).collect(Collectors.toList());

        // 获取社长姓名
        Map<Long, String> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getRealName));

        //批量统计每个社团的成员数量
        List<Long> clubIds = records.stream().map(Club::getId).collect(Collectors.toList());

        // 查询 club_member 表中状态为已入社(1)的记录，并按社团ID分组统计
        QueryWrapper<ClubMember> memberQuery = new QueryWrapper<>();
        memberQuery.select("club_id", "count(*) as count")
                .in("club_id", clubIds)
                .eq("status", 1) // 只统计正式成员
                .groupBy("club_id");

        List<Map<String, Object>> countMaps = clubMemberMapper.selectMaps(memberQuery);
        Map<Long, Integer> countMap = countMaps.stream().collect(Collectors.toMap(
                m -> Long.valueOf(m.get("club_id").toString()),
                m -> Integer.valueOf(m.get("count").toString())
        ));
        // 4.3 组装 VO
        List<ClubVO> voList = records.stream().map(club -> {
            ClubVO vo = new ClubVO();
            BeanUtil.copyProperties(club, vo); // Hutool 工具类拷贝属性
            vo.setPresidentName(userMap.getOrDefault(club.getCreatorId(), "未知"));
            vo.setMemberCount(countMap.getOrDefault(club.getId(), 0));
            return vo;
        }).collect(Collectors.toList());

        // 5. 重新封装分页结果
        Page<ClubVO> resultPage = new Page<>();
        BeanUtil.copyProperties(page, resultPage, "records");
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    public void applyJoin(Long clubId) {
        // 1. 获取当前登录用户 ID (从我们之前写的 UserContext 里拿)
        Long userId = UserContext.getUserId();

        // 2. 检查是否重复申请/已加入
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId);

        Long count = clubMemberMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("你已经加入或正在申请该社团，请勿重复操作");
        }

        // 3. 插入申请记录
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setMemberRole(1); // 1-普通成员
        member.setStatus(0);     // 0-申请中 (待社长审核)

        // MP 会自动填充雪花 ID 和创建时间
        clubMemberMapper.insert(member);
    }

    @Override
    public List<MyClubVO> getMyClubs() {
        Long userId = UserContext.getUserId();

        // 1. 查出我在 member 表里的所有记录
        LambdaQueryWrapper<ClubMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ClubMember::getUserId, userId);
        List<ClubMember> members = clubMemberMapper.selectList(memberWrapper);

        if (members.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取社团 IDs
        List<Long> clubIds = members.stream().map(ClubMember::getClubId).collect(Collectors.toList());

        // 3. 查出社团详细信息
        List<Club> clubs = this.listByIds(clubIds);
        Map<Long, Club> clubMap = clubs.stream().collect(Collectors.toMap(Club::getId, c -> c));

        // 4. 组装 VO
        return members.stream().map(m -> {
            MyClubVO vo = new MyClubVO();
            Club club = clubMap.get(m.getClubId());

            if (club != null) {
                vo.setClubId(club.getId());
                vo.setClubName(club.getName());
                vo.setClubCategory(club.getCategory());
            }

            vo.setMemberRole(m.getMemberRole());
            vo.setJoinStatus(m.getStatus()); // 注意这里字段名对应: Entity的status -> VO的joinStatus
            vo.setJoinTime(m.getJoinTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ClubMemberVO> getClubMembers(Long clubId, Integer status) {
        // 1. 安全检查：确保当前登录用户是这个社团的社长 (实际项目必须加，这里略过简化)

        // 2. 查询关联表
        LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubMember::getClubId, clubId);
        if (status != null) {
            wrapper.eq(ClubMember::getStatus, status);
        }
        // 把申请中的放在最前面
        wrapper.orderByAsc(ClubMember::getStatus).orderByDesc(ClubMember::getJoinTime);

        List<ClubMember> members = clubMemberMapper.selectList(wrapper);
        if (members.isEmpty()) return new ArrayList<>();

        // 3. 提取用户ID并查询用户信息
        List<Long> userIds = members.stream().map(ClubMember::getUserId).collect(Collectors.toList());
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 4. 组装 VO
        return members.stream().map(m -> {
            ClubMemberVO vo = new ClubMemberVO();
            cn.hutool.core.bean.BeanUtil.copyProperties(m, vo);
            User u = userMap.get(m.getUserId());
            if (u != null) {
                vo.setRealName(u.getRealName());
                vo.setUsername(u.getUsername());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void auditMember(Long memberId, Boolean pass) {
        ClubMember member = clubMemberMapper.selectById(memberId);
        if (member == null) throw new RuntimeException("记录不存在");

        // pass=true -> 1(正式), pass=false -> 2(拒绝)
        member.setStatus(pass ? 1 : 2);
        clubMemberMapper.updateById(member);
    }

    // 修改 ClubServiceImpl.java 中的相应部分
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveClubWithAdmin(Club club) {
        // 1. 设置初始状态为 0 (审核中)
        club.setStatus(0);
        // 2. 保存社团（Mybatis-Plus 会自动生成 ID）
        boolean saved = this.save(club);

        if (saved) {
            // 3. 将创建者添加为成员，且职位为社长(2)，状态为已入社(1)
            ClubMember admin = new ClubMember();
            admin.setClubId(club.getId());
            admin.setUserId(club.getCreatorId());
            admin.setMemberRole(2);
            admin.setStatus(1);
            clubMemberMapper.insert(admin);
        }
        return saved;
    }


}
