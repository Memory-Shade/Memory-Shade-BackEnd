package com.memoryshade.domain.goal.model;

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
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String category;

    @Column(name = "achieve_percent")
    private Integer achievePercent;

    @Column(name = "is_achieved")
    private boolean isAchieved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Goal(User user, String title, String category, LocalDateTime createdAt) {
        this.user = user;
        this.title = title;
        this.category = category;
        this.achievePercent = 0;
        this.isAchieved = false;
        this.createdAt = createdAt;
    }
}
