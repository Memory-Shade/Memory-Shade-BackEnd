package com.memoryshade.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.schedule.model.Schedule;
import com.memoryshade.domain.user.model.User;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ScheduleRequestDto(
        String title,

        @JsonProperty("alarm_time")
        LocalDateTime alarmTime //TODO: 현재보다 과거이면 안되도록 설정 추가 필요
) {
    public Schedule toSchedule(User user) {
        return Schedule.builder()
                .user(user)
                .title(title)
                .alarmTime(alarmTime)
                .build();
    }
}
