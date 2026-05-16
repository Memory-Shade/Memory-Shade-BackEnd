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

    String content,

    @JsonProperty("created_at")
    LocalDateTime createdAt,

    @JsonProperty("tts_url")
    String ttsUrl
) {
  public static ChatMessageResponseDto from(ChatMessage chatMessage) {
    return new ChatMessageResponseDto(
        chatMessage.getMessageId(),
        chatMessage.getSenderType(),
        chatMessage.getContent(),
        chatMessage.getCreatedAt(),
        buildTtsUrl(chatMessage)
    );
  }

  private static String buildTtsUrl(ChatMessage chatMessage) {
    if (chatMessage.getSenderType() != SenderType.AI) {
      return null;
    }

    return "/api/chat-sessions/%d/messages/%d/tts".formatted(
        chatMessage.getSession().getSessionId(),
        chatMessage.getMessageId()
    );
  }
}