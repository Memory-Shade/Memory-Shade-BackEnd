package com.memoryshade.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PushTokenUpdateResponseDto(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("expo_push_token")
        String expoPushToken
) {
}
