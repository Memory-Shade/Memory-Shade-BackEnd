package com.memoryshade.domain.schedule.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "alarm_time", nullable = false)
    private LocalDateTime alarmTime;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Schedule(User user, String title, LocalDateTime alarmTime) {
        this.user = user;
        this.title = title;
        this.alarmTime = alarmTime;
        this.notifiedAt = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAlarmTime(LocalDateTime alarmTime) {
        this.alarmTime = alarmTime;
        this.notifiedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void markNotified() {
        this.notifiedAt = LocalDateTime.now();
    }
}
