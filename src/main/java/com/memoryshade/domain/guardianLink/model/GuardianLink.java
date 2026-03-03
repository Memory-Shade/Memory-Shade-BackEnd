package com.memoryshade.domain.guardianLink.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "guardian_link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuardianLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public GuardianLink(User user, User guardian) {
        this.user = user;
        this.guardian = guardian;
        this.createdAt = LocalDateTime.now();
    }
}
