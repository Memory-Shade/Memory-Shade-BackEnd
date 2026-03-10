package com.memoryshade.domain.goal.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Goal(User user, String title) {
        this.user = user;
        this.title = title;
        this.createdAt = LocalDateTime.now();
    }
}
