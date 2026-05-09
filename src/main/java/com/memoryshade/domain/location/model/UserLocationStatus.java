package com.memoryshade.domain.location.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_location_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLocationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "last_latitude")
    private Double lastLatitude;

    @Column(name = "last_longitude")
    private Double lastLongitude;

    @Column(name = "is_outside_safe_zone", nullable = false)
    private boolean outsideSafeZone;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(name = "last_wandering_at")
    private LocalDateTime lastWanderingAt;

    public UserLocationStatus(User user) {
        this.user = user;
        this.outsideSafeZone = false;
    }

    public void updateLocation(Double latitude, Double longitude, boolean outsideSafeZone) {
        this.lastLatitude = latitude;
        this.lastLongitude = longitude;
        this.outsideSafeZone = outsideSafeZone;
        this.lastCheckedAt = LocalDateTime.now();
    }

    public void markWandering() {
        this.lastWanderingAt = LocalDateTime.now();
    }
}
