package com.memoryshade.domain.guardianLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.user.model.User;

public record GuardianLinkGetResponseDto(
        @JsonProperty("user_id")
        Long userId,

        String name,

        @JsonProperty("phone_number")
        String phoneNumber
) {
    public static GuardianLinkGetResponseDto fromUser(User user) {
        return new GuardianLinkGetResponseDto(
                user.getUserId(),
                user.getName(),
                user.getPhoneNumber()
        );
    }
}
