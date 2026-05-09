package com.memoryshade.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record PushTokenUpdateRequestDto(
        @JsonProperty("expo_push_token")
        @NotBlank(message = "Expo Push Token을 입력해주세요")
        String expoPushToken
) {
}
