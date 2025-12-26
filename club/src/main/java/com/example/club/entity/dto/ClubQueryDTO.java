package com.example.club.entity.dto;

public record ClubQueryDTO(
        Integer page,   // 当前页码 (默认1)
        Integer size,   // 每页条数 (默认10)
        String name     // 搜索关键词
) {}