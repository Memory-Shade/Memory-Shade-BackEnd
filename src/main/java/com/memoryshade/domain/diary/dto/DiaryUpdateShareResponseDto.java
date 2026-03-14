package com.memoryshade.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.diary.model.Diary;

public record DiaryUpdateShareResponseDto(

        @JsonProperty("is_shared")
        boolean isShared
) {
    public static DiaryUpdateShareResponseDto fromDiary(Diary diary) {
        return new DiaryUpdateShareResponseDto(diary.isShared());
    }
}
