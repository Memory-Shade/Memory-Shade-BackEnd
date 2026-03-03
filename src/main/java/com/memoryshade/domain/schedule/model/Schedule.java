package com.memoryshade.domain.schedule.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(name = "alarm_time")
    private LocalDateTime alarmTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Schedule(User user, String title, LocalDateTime alarmTime, LocalDateTime createdAt) {
        this.user = user;
        this.title = title;
        this.alarmTime = alarmTime;
        this.createdAt = createdAt;
    }
}
