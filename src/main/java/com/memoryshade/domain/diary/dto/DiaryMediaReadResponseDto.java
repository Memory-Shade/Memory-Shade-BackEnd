package com.memoryshade.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.diary.model.DiaryMedia;
import com.memoryshade.domain.diary.model.MediaType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DiaryMediaReadResponseDto(
    @JsonProperty("media_id")
    Long mediaId,

    @JsonProperty("media_url")
    String mediaUrl,

    @JsonProperty("media_type")
    MediaType mediaType
) {
  public static DiaryMediaReadResponseDto fromDiaryMedia(DiaryMedia diaryMedia) {
    return new DiaryMediaReadResponseDto(
        diaryMedia.getMediaId(),
        diaryMedia.getMediaUrl(),
        diaryMedia.getMediaType()
    );
  }
}