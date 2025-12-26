package com.example.club.controller;

import com.example.club.common.Result;
import com.example.club.entity.User;
import com.example.club.entity.dto.LoginDTO;
import com.example.club.entity.dto.RegisterDTO;
import com.example.club.service.IUserService;
import com.example.club.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    // 注册接口
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        try {
            log.info("register user: {}", dto.username());
            userService.register(dto);
            return Result.success("注册成功，请登录");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    // 登录接口
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {
        try {
            log.info("login user:{}", dto.username());
            String token = userService.login(dto);
            return Result.success(token);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }


    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        // 1. 从拦截器存的上下文里拿到当前用户ID
        Long userId = UserContext.getUserId();

        // 2. 查询数据库
        User user = userService.getById(userId);

        if (user != null) {
            // 3. 安全起见，把密码擦除，不返回给前端
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.fail("用户不存在");
    }
}
