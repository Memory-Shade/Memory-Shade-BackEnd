package com.memoryshade.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;

public record AuthResponseDto(

        @JsonProperty("user_id")
        Long userId,
        String name,
        Role role,

        @JsonProperty("access_token")
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
