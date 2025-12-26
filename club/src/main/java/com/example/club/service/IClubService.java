package com.example.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.entity.Club;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.entity.dto.ClubQueryDTO;
import com.example.club.entity.vo.ClubMemberVO;
import com.example.club.entity.vo.ClubVO;
import com.example.club.entity.vo.MyClubVO;

import java.util.List;

/**
 * <p>
 * 社团主表 服务类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
public interface IClubService extends IService<Club> {

    Page<ClubVO> listClubs(ClubQueryDTO queryDTO);

    void applyJoin(Long clubId);

    List<MyClubVO> getMyClubs();

    List<ClubMemberVO> getClubMembers(Long clubId, Integer status);

    void auditMember(Long memberId, Boolean pass);

    boolean saveClubWithAdmin(Club club);

}
