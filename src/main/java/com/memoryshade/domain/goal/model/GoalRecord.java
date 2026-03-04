package com.memoryshade.domain.goal.model;

import com.memoryshade.domain.diary.model.Diary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "goal_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Column(name = "is_achieved", nullable = false)
    private boolean isAchieved;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public GoalRecord(Goal goal, Diary diary, boolean isAchieved) {
        this.goal = goal;
        this.diary = diary;
        this.isAchieved = isAchieved;
        this.createdAt = LocalDateTime.now();
    }
}
