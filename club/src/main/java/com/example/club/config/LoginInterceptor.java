package com.example.club.config;


import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.example.club.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 必须和生成 Token 时的密钥一致
    private static final byte[] JWT_KEY = "My_Club_Secret_Key_123456".getBytes();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 1. 获取请求头中的 token
        String token = request.getHeader("Authorization");


        // 2. 如果没有 token，拦截
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        try {
            // 3. 校验 token 签名是否正确
            boolean verify = JWTUtil.verify(token, JWT_KEY);
            if (!verify) {
                response.setStatus(401);
                return false;
            }

            // 4. 解析 token 中的 userId
            JWT jwt = JWTUtil.parseToken(token);
            // 注意：Hutool 解析出来可能是 Integer，建议转一下
            Long userId = Long.valueOf(jwt.getPayload("uid").toString());

            // 5. 【关键】存入 ThreadLocal，供后续 Controller 使用
            UserContext.setUserId(userId);

            return true; // 放行

        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求处理完毕，清理线程变量，防止内存泄漏
        UserContext.remove();
    }
}