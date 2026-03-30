package com.memoryshade.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatMessagesReadResponseDto(
    @JsonProperty("messages")
    List<ChatMessageResponseDto> messages,

    @JsonProperty("medias")
    List<ChatMediaReadResponseDto> medias
) {
}