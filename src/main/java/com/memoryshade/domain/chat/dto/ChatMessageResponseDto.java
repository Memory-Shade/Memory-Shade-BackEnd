package com.memoryshade.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.chat.model.ChatMessage;
import com.memoryshade.domain.chat.model.ChatMessageType;
import com.memoryshade.domain.chat.model.SenderType;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatMessageResponseDto(
    @JsonProperty("message_id")
    Long messageId,

    @JsonProperty("sender_type")
    SenderType senderType,

    @JsonProperty("message_type")
    ChatMessageType messageType,

    @JsonProperty("content")
    String content,

    @JsonProperty("reference_media_url")
    String referenceMediaUrl,

    @JsonProperty("created_at")
    LocalDateTime createdAt
) {
  public static ChatMessageResponseDto from(ChatMessage chatMessage) {
    return new ChatMessageResponseDto(
        chatMessage.getMessageId(),
        chatMessage.getSenderType(),
        chatMessage.getMessageType(),
        chatMessage.getContent(),
        chatMessage.getReferenceMediaUrl(),
        chatMessage.getCreatedAt()
    );
  }
}