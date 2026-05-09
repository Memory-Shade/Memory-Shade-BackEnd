package com.memoryshade.domain.location.model;

import com.memoryshade.domain.gps.model.Gps;
import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wandering_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WanderingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Gps safeZone;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "distance_meter")
    private Double distanceMeter;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    public WanderingEvent(User user, Gps safeZone, Double latitude, Double longitude, Double distanceMeter) {
        this.user = user;
        this.safeZone = safeZone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceMeter = distanceMeter;
        this.occurredAt = LocalDateTime.now();
    }
}
