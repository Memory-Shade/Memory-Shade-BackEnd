package com.memoryshade.domain.recall.dto;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.model.DiaryMedia;

public record RecallQuizQuestionCreateDto(
    int questionOrder,
    String questionText,
    String expectedAnswer,
    Diary sourceDiary,
    DiaryMedia sourceDiaryMedia
) {
}