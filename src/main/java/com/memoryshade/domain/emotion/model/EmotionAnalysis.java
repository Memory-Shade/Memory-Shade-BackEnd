package com.memoryshade.domain.emotion.model;

import com.memoryshade.domain.diary.model.Diary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "emotion_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Column(name = "joy_score")
    private Integer joyScore;

    @Column(name = "sadness_score")
    private Integer sadnessScore;

    @Column(name = "anger_score")
    private Integer angerScore;

    @Column(name = "anxiety_score") // 추가
    private Integer anxietyScore;

    @Column(name = "embarrassment_score") // 추가
    private Integer embarrassmentScore;

    @Column(name = "hurt_score") // 추가
    private Integer hurtScore;

    @Column(name = "neutral_score")
    private Integer neutralScore;

    @Column(name = "analyzed_at", nullable = false, updatable = false)
    private LocalDateTime analyzedAt;

    @Builder
    public EmotionAnalysis(Diary diary, Integer joyScore, Integer sadnessScore, Integer angerScore,
            Integer anxietyScore, Integer embarrassmentScore, Integer hurtScore, Integer neutralScore) {
            this.diary = diary;
            this.joyScore = joyScore;
            this.sadnessScore = sadnessScore;
            this.angerScore = angerScore;
            this.anxietyScore = anxietyScore;
            this.embarrassmentScore = embarrassmentScore;
            this.hurtScore = hurtScore;
            this.neutralScore = neutralScore;
            this.analyzedAt = LocalDateTime.now();
        }
}
