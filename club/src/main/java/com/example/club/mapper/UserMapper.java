package com.example.club.mapper;

import com.example.club.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
