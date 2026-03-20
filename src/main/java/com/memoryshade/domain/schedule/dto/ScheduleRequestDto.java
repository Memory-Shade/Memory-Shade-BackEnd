package com.memoryshade.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.schedule.model.Schedule;
import com.memoryshade.domain.user.model.User;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleRequestDto(

        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        String title,

        @NotNull(message = "알림 시간은 필수입니다.")
        @Future(message = "알림 시간은 현재 이후여야 합니다.")
        @JsonProperty("alarm_time")
        LocalDateTime alarmTime
) {
    public Schedule toSchedule(User user) {
        return Schedule.builder()
                .user(user)
                .title(title)
                .alarmTime(alarmTime)
                .build();
    }
}
