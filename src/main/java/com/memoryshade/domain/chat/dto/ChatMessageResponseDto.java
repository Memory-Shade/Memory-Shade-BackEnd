package com.memoryshade.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.chat.model.ChatMessage;
import com.memoryshade.domain.chat.model.SenderType;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatMessageResponseDto(
    @JsonProperty("message_id")
    Long messageId,

    @JsonProperty("sender_type")
    SenderType senderType,

    @JsonProperty("content")
    String content,

    @JsonProperty("created_at")
    LocalDateTime createdAt
) {
  public static ChatMessageResponseDto from(ChatMessage message) {
    return new ChatMessageResponseDto(
        message.getMessageId(),
        message.getSenderType(),
        message.getContent(),
        message.getCreatedAt()
    );
  }
}