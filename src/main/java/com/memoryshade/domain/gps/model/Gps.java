package com.memoryshade.domain.gps.model;

import com.memoryshade.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gps_safe_zones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    private String title;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "radius_meter", nullable = false)
    private Integer radiusMeter;

    @Builder
    public Gps(User user, User guardian, Double latitude, String title, Double longitude, Integer radiusMeter) {
        this.user = user;
        this.guardian = guardian;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMeter = radiusMeter;
    }

    public void updateSafeZone(String title, Double latitude, Double longitude, Integer radiusMeter) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMeter = radiusMeter;
    }
}
