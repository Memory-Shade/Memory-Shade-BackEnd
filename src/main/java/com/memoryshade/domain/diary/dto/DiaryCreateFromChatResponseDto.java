package com.memoryshade.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.diary.model.Diary;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DiaryCreateFromChatResponseDto(
    @JsonProperty("diary_id")
    Long diaryId,

    @JsonProperty("diary_date")
    LocalDate diaryDate,

    @JsonProperty("content_summary")
    String contentSummary
) {
  public static DiaryCreateFromChatResponseDto fromDiary(Diary diary) {
    return new DiaryCreateFromChatResponseDto(
        diary.getDiaryId(),
        diary.getDiaryDate(),
        diary.getContentSummary()
    );
  }
}