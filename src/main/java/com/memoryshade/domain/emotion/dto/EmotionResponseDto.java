package com.memoryshade.domain.emotion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.emotion.model.EmotionAnalysis;
import com.memoryshade.domain.emotion.model.EmotionType;
import java.util.Map;

public record EmotionResponseDto(
    @JsonProperty("top_emotion") String topEmotion,
    Double confidence,
    Map<String, Object> emotions
) {

    public EmotionAnalysis toEntity(Diary diary) {
        return EmotionAnalysis.builder()
            .diary(diary)
            .joyScore(convert(EmotionType.JOY))
            .sadnessScore(convert(EmotionType.SADNESS))
            .angerScore(convert(EmotionType.ANGER))
            .anxietyScore(convert(EmotionType.ANXIETY))
            .embarrassmentScore(convert(EmotionType.EMBARRASSMENT))
            .hurtScore(convert(EmotionType.HURT))
            .neutralScore(convert(EmotionType.NEUTRAL))
            .build();
    }

    private Integer convert(EmotionType type) {
        Object val = emotions.get(type.getKoreanName());
        if (val instanceof Number n) return n.intValue();
        return 0;
    }
}