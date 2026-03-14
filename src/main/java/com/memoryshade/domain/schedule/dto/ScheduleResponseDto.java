package com.memoryshade.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.schedule.model.Schedule;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ScheduleResponseDto(

        @JsonProperty("schedule_id")
        Long scheduleId,

        @JsonProperty("user_id")
        Long userId,
        String title,

        @JsonProperty("alarm_time")
        LocalDateTime alarmTime,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
    public static ScheduleResponseDto fromSchedule(Schedule schedule) {
        return new ScheduleResponseDto(
                schedule.getScheduleId(),
                schedule.getUser().getUserId(),
                schedule.getTitle(),
                schedule.getAlarmTime(),
                schedule.getUpdatedAt()
        );
    }
}
