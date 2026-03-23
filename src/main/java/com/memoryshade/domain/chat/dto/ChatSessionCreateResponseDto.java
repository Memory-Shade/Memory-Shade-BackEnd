package com.memoryshade.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatSessionCreateResponseDto(
    @JsonProperty("session_id")
    Long sessionId,

    @JsonProperty("session_date")
    LocalDate sessionDate,

    @JsonProperty("messages")
    List<ChatMessageResponseDto> messages
) {
}