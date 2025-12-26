package com.example.club.service.impl;

import com.example.club.entity.ClubMember;
import com.example.club.mapper.ClubMemberMapper;
import com.example.club.service.IClubMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
