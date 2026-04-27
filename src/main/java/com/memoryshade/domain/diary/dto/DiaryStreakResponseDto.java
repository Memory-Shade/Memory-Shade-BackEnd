package com.memoryshade.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record DiaryStreakResponseDto(
        @JsonProperty("current_streak_days")
        int currentStreakDays,

        @JsonProperty("base_date")
        LocalDate baseDate,

        @JsonProperty("last_diary_date")
        LocalDate lastDiaryDate
) {
}
