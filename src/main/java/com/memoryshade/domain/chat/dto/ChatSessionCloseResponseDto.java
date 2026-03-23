package com.memoryshade.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatSessionCloseResponseDto(
    @JsonProperty("diary_id")
    Long diaryId,

    @JsonProperty("diary_date")
    LocalDate diaryDate,

    @JsonProperty("content_summary")
    String contentSummary
) {
}