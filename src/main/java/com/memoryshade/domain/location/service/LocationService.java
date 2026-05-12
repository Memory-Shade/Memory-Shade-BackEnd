package com.memoryshade.domain.location.service;

import com.memoryshade.domain.gps.model.Gps;
import com.memoryshade.domain.gps.repository.GpsRepository;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.location.dto.DailyWanderingCountResponseDto;
import com.memoryshade.domain.location.dto.LocationUpdateRequestDto;
import com.memoryshade.domain.location.dto.LocationUpdateResponseDto;
import com.memoryshade.domain.location.dto.WeeklyWanderingAverageResponseDto;
import com.memoryshade.domain.location.exception.LocationErrorCode;
import com.memoryshade.domain.location.model.UserLocationStatus;
import com.memoryshade.domain.location.model.WanderingEvent;
import com.memoryshade.domain.location.repository.UserLocationStatusRepository;
import com.memoryshade.domain.location.repository.WanderingEventRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private static final int EARTH_RADIUS_METER = 6371000;

    private final UserRepository userRepository;
    private final GpsRepository gpsRepository;
    private final GuardianLinkRepository guardianLinkRepository;
    private final UserLocationStatusRepository userLocationStatusRepository;
    private final WanderingEventRepository wanderingEventRepository;

    @Transactional
    public LocationUpdateResponseDto updateMyLocation(Long loginUserId, LocationUpdateRequestDto request) {
        validateAuthenticated(loginUserId);

        User user = userRepository.getByUserId(loginUserId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(LocationErrorCode.USER_ONLY);
        }

        List<Gps> safeZones = gpsRepository.findAllByUser_UserId(loginUserId);
        if (safeZones.isEmpty()) {
            throw new ExceptionList(LocationErrorCode.SAFE_ZONE_NOT_FOUND);
        }

        SafeZoneCheckResult checkResult = checkOutsideAllSafeZones(
                safeZones,
                request.latitude(),
                request.longitude()
        );

        UserLocationStatus status = userLocationStatusRepository
                .findByUser_UserId(loginUserId)
                .orElseGet(() -> new UserLocationStatus(user));

        boolean previousOutsideSafeZone = status.isOutsideSafeZone();
        boolean wanderingDetected = !previousOutsideSafeZone && checkResult.outsideAllSafeZones();

        if (wanderingDetected) {
            wanderingEventRepository.save(
                    new WanderingEvent(
                            user,
                            checkResult.nearestSafeZone(),
                            request.latitude(),
                            request.longitude(),
                            checkResult.distanceMeter()
                    )
            );
            status.markWandering();
        }

        status.updateLocation(request.latitude(), request.longitude(), checkResult.outsideAllSafeZones());
        userLocationStatusRepository.save(status);

        return new LocationUpdateResponseDto(
                loginUserId,
                checkResult.outsideAllSafeZones(),
                previousOutsideSafeZone,
                wanderingDetected,
                checkResult.nearestSafeZone() == null ? null : checkResult.nearestSafeZone().getZoneId(),
                checkResult.nearestSafeZone() == null ? null : checkResult.nearestSafeZone().getRadiusMeter(),
                checkResult.distanceMeter(),
                wanderingEventRepository.countByUser_UserId(loginUserId)
        );
    }

    public WeeklyWanderingAverageResponseDto getWeeklyWanderingAverage(Long loginUserId, Long userId, LocalDate baseDate) {
        validateAuthenticated(loginUserId);
        validateGuardianCanAccessUser(loginUserId, userId);

        LocalDate targetDate = baseDate == null ? LocalDate.now() : baseDate;
        LocalDate startDate = targetDate.with(DayOfWeek.MONDAY);
        LocalDate endDate = targetDate.with(DayOfWeek.SUNDAY);

        long weeklyCount = wanderingEventRepository.countByUser_UserIdAndOccurredAtBetween(
                userId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        return new WeeklyWanderingAverageResponseDto(
                userId,
                startDate,
                endDate,
                weeklyCount / 7.0
        );
    }

    public DailyWanderingCountResponseDto getDailyWanderingCount(Long loginUserId, Long userId, LocalDate date) {
        validateAuthenticated(loginUserId);
        validateGuardianCanAccessUser(loginUserId, userId);

        LocalDate targetDate = date == null ? LocalDate.now() : date;
        long dailyCount = wanderingEventRepository.countByUser_UserIdAndOccurredAtBetween(
                userId,
                targetDate.atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay()
        );

        return new DailyWanderingCountResponseDto(
                userId,
                targetDate,
                dailyCount
        );
    }

    private SafeZoneCheckResult checkOutsideAllSafeZones(List<Gps> safeZones, double latitude, double longitude) {
        Gps nearestOutsideZone = null;
        double nearestDistanceFromRadius = Double.MAX_VALUE;
        double nearestDistanceMeter = 0;

        for (Gps safeZone : safeZones) {
            double distance = calculateDistance(
                    safeZone.getLatitude(),
                    safeZone.getLongitude(),
                    latitude,
                    longitude
            );

            if (distance <= safeZone.getRadiusMeter()) {
                return SafeZoneCheckResult.inside(distance, safeZone);
            }

            double distanceFromRadius = distance - safeZone.getRadiusMeter();
            if (distanceFromRadius < nearestDistanceFromRadius) {
                nearestDistanceFromRadius = distanceFromRadius;
                nearestDistanceMeter = distance;
                nearestOutsideZone = safeZone;
            }
        }

        return SafeZoneCheckResult.outside(nearestDistanceMeter, nearestOutsideZone);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METER * c;
    }

    private void validateAuthenticated(Long loginUserId) {
        if (loginUserId == null) {
            throw new ExceptionList(LocationErrorCode.UNAUTHORIZED_USER);
        }
    }

    private void validateGuardianCanAccessUser(Long guardianId, Long userId) {
        User guardian = userRepository.getByUserId(guardianId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(LocationErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(LocationErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, guardianId);
    }

    private record SafeZoneCheckResult(
            boolean outsideAllSafeZones,
            double distanceMeter,
            Gps nearestSafeZone
    ) {

        private static SafeZoneCheckResult inside(double distanceMeter, Gps safeZone) {
            return new SafeZoneCheckResult(false, distanceMeter, safeZone);
        }

        private static SafeZoneCheckResult outside(double distanceMeter, Gps safeZone) {
            return new SafeZoneCheckResult(true, distanceMeter, safeZone);
        }
    }
}
