package com.example.club.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.club.entity.ClubMember;
import com.example.club.mapper.ClubMemberMapper;
import com.example.club.service.IClubMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 社团成员关联表 服务实现类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Service
public class ClubMemberServiceImpl extends ServiceImpl<ClubMemberMapper, ClubMember> implements IClubMemberService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quitClub(Long clubId) {
        Long userId = UserContext.getUserId();

        // 1. 校验身份：如果是社长，禁止退出
        LambdaQueryWrapper<ClubMember> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId);

        ClubMember member = this.getOne(checkWrapper);
        if (member == null) {
            throw new RuntimeException("你不是该社团成员");
        }

        // memberRole 为 2 代表社长
        if (member.getMemberRole() != null && member.getMemberRole() == 2) {
            throw new RuntimeException("社长不能直接退出社团，请先转让职位或解散社团");
        }

        // 2. 执行删除记录（退出）
        this.remove(checkWrapper);
    }
}
