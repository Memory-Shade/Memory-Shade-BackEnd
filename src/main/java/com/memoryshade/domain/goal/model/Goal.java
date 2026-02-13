package com.memoryshade.domain.goal.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer goalId;

    @Column(name = "user_id")
    private Integer userId;

    private String title;

    private String category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_achieved")
    private boolean isAchieved;
}
