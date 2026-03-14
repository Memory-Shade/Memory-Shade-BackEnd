package com.memoryshade.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.diary.model.Diary;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DiaryReadResponseDto(

        @JsonProperty("diary_id")
        Long diaryId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("content_summary")
        String contentSummary,

        @JsonProperty("diary_date")
        LocalDate diaryDate,

        @JsonProperty("is_shared")
        boolean isShared,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static DiaryReadResponseDto fromDiary(Diary diary) {
        return new DiaryReadResponseDto(
                diary.getDiaryId(),
                diary.getUser().getUserId(),
                diary.getContentSummary(),
                diary.getDiaryDate(),
                diary.isShared(),
                diary.getCreatedAt()
        );
    }
}
