package com.example.club.service;

import com.example.club.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.entity.dto.LoginDTO;
import com.example.club.entity.dto.RegisterDTO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
public interface IUserService extends IService<User> {

    void register(RegisterDTO dto);

    String login(LoginDTO dto);

    void updatePassword(String oldPassword, String newPassword);
}
