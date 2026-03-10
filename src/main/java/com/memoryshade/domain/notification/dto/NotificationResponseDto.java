package com.memoryshade.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.notification.model.NotiType;
import com.memoryshade.domain.notification.model.Notification;

@JsonNaming(value = SnakeCaseStrategy.class)
public record NotificationResponseDto(

        @JsonProperty("noti_id")
        Long notiId,

        @JsonProperty("user_id")
        Long userId,
        String content,

        @JsonProperty("noti_type")
        NotiType notiType,

        @JsonProperty("is_read")
        boolean isRead
) {
    public static NotificationResponseDto fromNotification(Notification noti) {
        return new NotificationResponseDto(
                noti.getNotiId(),
                noti.getUser().getUserId(),
                noti.getContent(),
                noti.getNotiType(),
                noti.isRead()
        );
    }
}
