package com.memoryshade.domain.game.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "played_at", nullable = false, updatable = false)
    private LocalDateTime playedAt;

    @Builder
    public Game(User user, Integer score) {
        this.user = user;
        this.score = score;
        this.playedAt = LocalDateTime.now();
    }

    public void updateScore(Integer score) {
        this.score = score;
    }
}
