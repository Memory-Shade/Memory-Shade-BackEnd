package com.memoryshade.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.notification.model.NotiType;
import com.memoryshade.domain.notification.model.Notification;
import com.memoryshade.domain.user.model.User;

public record NotificationCreateRequestDto(
        String content,

        @JsonProperty("noti_type")
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
