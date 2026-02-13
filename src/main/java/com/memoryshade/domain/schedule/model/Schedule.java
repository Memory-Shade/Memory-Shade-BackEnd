package com.memoryshade.domain.schedule.model;

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

    @Column(name = "user_id")
    private Integer userId;

    private String title;

    @Column(name = "alarm_time")
    private LocalDateTime alarmTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Schedule(String title, LocalDateTime alarmTime, LocalDateTime createdAt) {
        this.title = title;
        this.alarmTime = alarmTime;
        this.createdAt = createdAt;
    }
}
