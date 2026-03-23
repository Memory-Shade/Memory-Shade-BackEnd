package com.memoryshade.domain.guardianLink.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "guardian_link_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuardianLinkRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GuardianLinkRequestStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Builder
    public GuardianLinkRequest(User user, User guardian) {
        this.user = user;
        this.guardian = guardian;
        this.status = GuardianLinkRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void accept() {
        this.status = GuardianLinkRequestStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = GuardianLinkRequestStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }
}
