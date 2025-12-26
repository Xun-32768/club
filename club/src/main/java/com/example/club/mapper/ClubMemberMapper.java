package com.example.club.mapper;

import com.example.club.entity.ClubMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 社团成员关联表 Mapper 接口
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {

}
