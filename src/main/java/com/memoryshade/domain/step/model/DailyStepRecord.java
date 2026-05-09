package com.memoryshade.domain.step.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_step_records",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_step_user_date",
                        columnNames = {"user_id", "record_date"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStepRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "step_count", nullable = false)
    private Integer stepCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public DailyStepRecord(User user, LocalDate recordDate, Integer stepCount) {
        this.user = user;
        this.recordDate = recordDate;
        this.stepCount = stepCount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStepCount(Integer stepCount) {
        this.stepCount = stepCount;
        this.updatedAt = LocalDateTime.now();
    }
}
