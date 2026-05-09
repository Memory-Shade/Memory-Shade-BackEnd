package com.memoryshade.domain.step.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.step.model.DailyStepRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyStepResponseDto(

        @JsonProperty("record_id")
        Long recordId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("record_date")
        LocalDate recordDate,

        @JsonProperty("step_count")
        Integer stepCount,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
    public static DailyStepResponseDto from(DailyStepRecord record) {
        return new DailyStepResponseDto(
                record.getRecordId(),
                record.getUser().getUserId(),
                record.getRecordDate(),
                record.getStepCount(),
                record.getUpdatedAt()
        );
    }

    public static DailyStepResponseDto empty(Long userId, LocalDate recordDate) {
        return new DailyStepResponseDto(
                null,
                userId,
                recordDate,
                0,
                null
        );
    }
}
