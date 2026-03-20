package com.memoryshade.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.notification.model.NotiType;
import com.memoryshade.domain.notification.model.Notification;
import com.memoryshade.domain.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationCreateRequestDto(

        @NotBlank(message = "알림 내용은 비어 있을 수 없습니다.")
        @Size(max = 255, message = "알림 내용은 255자를 초과할 수 없습니다.")
        String content,

        @JsonProperty("noti_type")
        @NotNull(message = "알림 타입은 필수입니다.")
        NotiType notiType
) {
    public Notification toNotification(User user) {
        return Notification.builder()
                .user(user)
                .content(content)
                .notiType(notiType)
                .build();
    }
}
