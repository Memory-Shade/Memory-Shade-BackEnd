package com.memoryshade.domain.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StatusSummaryResponseDto(
    String summary,

    @JsonProperty("generated_at")
    LocalDateTime generatedAt
) {
}