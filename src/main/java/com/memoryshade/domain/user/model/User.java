package com.memoryshade.domain.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String password;

    private String name;

    private String fcmToken;

    private LocalDateTime createdAt;

    private LocalDateTime lastLoggedAt;

    @Builder
    private User(String phoneNumber, String password, String name, String fcmToken, LocalDateTime createdAt, LocalDateTime lastLoggedAt) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
        this.fcmToken = fcmToken;
        this.createdAt = createdAt;
        this.lastLoggedAt = lastLoggedAt;
    }
}
