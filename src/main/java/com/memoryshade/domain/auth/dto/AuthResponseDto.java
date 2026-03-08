package com.memoryshade.domain.auth.dto;

import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;

public record AuthResponseDto(
        Long userId,
        String name,
        Role role,
        String accessToken
) {
    public static AuthResponseDto of(User user, String accessToken) {
        return new AuthResponseDto(
                user.getUserId(),
                user.getName(),
                user.getRole(),
                accessToken);
    }
}
