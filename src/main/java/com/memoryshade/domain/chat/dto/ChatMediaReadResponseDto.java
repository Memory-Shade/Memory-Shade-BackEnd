package com.memoryshade.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.chat.model.ChatSessionMedia;
import com.memoryshade.domain.diary.model.MediaType;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatMediaReadResponseDto(
    @JsonProperty("media_url")
    String mediaUrl,

    @JsonProperty("media_type")
    MediaType mediaType,

    @JsonProperty("created_at")
    LocalDateTime createdAt
) {
  public static ChatMediaReadResponseDto from(ChatSessionMedia chatSessionMedia) {
    return new ChatMediaReadResponseDto(
        chatSessionMedia.getMediaUrl(),
        chatSessionMedia.getMediaType(),
        chatSessionMedia.getCreatedAt()
    );
  }
}