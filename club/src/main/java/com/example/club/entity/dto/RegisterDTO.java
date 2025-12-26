package com.example.club.entity.dto;

public record RegisterDTO(
        String username,
        String password,
        String realName
) {}