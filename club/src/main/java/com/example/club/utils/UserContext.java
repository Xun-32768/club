package com.example.club.utils;

public class UserContext {
    // 使用 ThreadLocal 确保每个线程（每个请求）的数据是独立的
    private static final ThreadLocal<Long> userHolder = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userHolder.set(userId);
    }

    public static Long getUserId() {
        return userHolder.get();
    }

    // 请求结束时必须清理，防止内存泄漏
    public static void remove() {
        userHolder.remove();
    }
}