package com.memoryshade.domain.auth.dto;

public record AuthResponseDto(
        Long userId,
        String name,
        String accessToken
) {}
