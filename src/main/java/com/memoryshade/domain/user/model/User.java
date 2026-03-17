package com.memoryshade.domain.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String fcmToken;

    private LocalDateTime createdAt;

    private LocalDateTime lastLoggedAt;

    @Builder
    private User(String phoneNumber, String password, String name, Role role, String fcmToken, LocalDateTime createdAt, LocalDateTime lastLoggedAt) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.role = role;
        this.fcmToken = fcmToken;
        this.createdAt = createdAt;
        this.lastLoggedAt = lastLoggedAt;
    }

    public void updateLastLoggedAt() {
        this.lastLoggedAt = LocalDateTime.now();
    }

    public void clearFcmToken() {
        this.fcmToken = null;
    }
}
