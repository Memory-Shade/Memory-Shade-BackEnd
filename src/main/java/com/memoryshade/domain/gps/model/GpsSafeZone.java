package com.memoryshade.domain.gps.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gps_safe_zone")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GpsSafeZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "radius_meter", nullable = false)
    private Integer radiusMeter;

    @Builder
    public GpsSafeZone(User user, User guardian, Double latitude, Double longitude, Integer radiusMeter) {
        this.user = user;
        this.guardian = guardian;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMeter = radiusMeter;
    }

    public void updateSafeZone(Double latitude, Double longitude, Integer radiusMeter) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMeter = radiusMeter;
    }
}
