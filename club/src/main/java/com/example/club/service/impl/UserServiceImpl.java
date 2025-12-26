package com.example.club.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.club.entity.User;
import com.example.club.entity.dto.LoginDTO;
import com.example.club.entity.dto.RegisterDTO;
import com.example.club.mapper.UserMapper;
import com.example.club.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    // JWT 密钥 (正式项目请配置在 application.yml 中)
    private static final byte[] JWT_KEY = "My_Club_Secret_Key_123456".getBytes();

    /**
     * 用户注册
     */
    @Override
    public void register(RegisterDTO dto) {
        // 1. 校验账号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.username());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("该学号已注册");
        }

        // 2. 封装用户对象
        User user = new User();
        user.setUsername(dto.username());
        user.setRealName(dto.realName());
        // 3. 密码加密 (MD5)
        user.setPassword(SecureUtil.md5(dto.password()));
        // 4. 【关键】强制设为普通学生 (1)
        user.setSystemRole(1);

        this.save(user);
    }

    /**
     * 用户登录
     * @return 生成的 JWT Token
     */
    @Override
    public String login(LoginDTO dto) {
        // 1. 根据学号查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.username());
        User user = this.getOne(wrapper);

        // 2. 校验账号是否存在 & 密码是否匹配 (将输入的密码加密后比对)
        if (user == null || !user.getPassword().equals(SecureUtil.md5(dto.password()))) {
            throw new RuntimeException("账号或密码错误");
        }

        // 3. 校验账号是否被封禁 (逻辑删除或状态位)
        // if (user.getDeleted() == 1) ...

        // 4. 生成 JWT Token (包含: 用户ID, 角色, 过期时间)
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", user.getId());
        payload.put("role", user.getSystemRole());
        // 签发时间: 现在, 过期时间: 24小时后
        String token = JWTUtil.createToken(payload, JWT_KEY);

        return token;
    }
}
