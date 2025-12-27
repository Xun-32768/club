package com.example.club.service;

import com.example.club.entity.ClubMember;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 社团成员关联表 服务类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
public interface IClubMemberService extends IService<ClubMember> {
    void quitClub(Long clubId);
}
