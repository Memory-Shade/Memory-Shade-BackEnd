package com.memoryshade.domain.gps.service;

import com.memoryshade.domain.gps.dto.GpsRequestDto;
import com.memoryshade.domain.gps.dto.GpsResponseDto;
import com.memoryshade.domain.gps.exception.GpsErrorCode;
import com.memoryshade.domain.gps.model.Gps;
import com.memoryshade.domain.gps.repository.GpsRepository;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GpsService {

    private final GpsRepository gpsRepository;
    private final UserRepository userRepository;
    private final GuardianLinkRepository guardianLinkRepository;

    public GpsResponseDto Create(Long loginUserId, Long userId, GpsRequestDto request) {

        if (loginUserId == null) {
            throw new ExceptionList(GpsErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        User guardian = userRepository.getByUserId(loginUserId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GpsErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GpsErrorCode.TARGET_USER_ONLY);
        }

        //TODO: 어차피 위에 걸려서 필요없을거 같긴 한데
        if (guardian.getUserId().equals(user.getUserId())) {
            throw new ExceptionList(GpsErrorCode.SELF_SET_NOT_ALLOWED);
        }

        Gps gps = gpsRepository.save(request.toGps(user, guardian));

        return GpsResponseDto.fromGps(gps);
    }

    public List<GpsResponseDto> getUserGps(Long loginUserId, Long userId) {
        if (loginUserId == null) {
            throw new ExceptionList(GpsErrorCode.UNAUTHORIZED_GUARDIAN);
        }

        User guardian = userRepository.getByUserId(loginUserId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GpsErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GpsErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, loginUserId);

        return gpsRepository.findAllByUser_UserId(userId)
                .stream()
                .map(GpsResponseDto::fromGps)
                .toList();
    }

    public GpsResponseDto update(Long loginUserId, Long userId, Long zoneId, GpsRequestDto request) {
        if (loginUserId == null) {
            throw new ExceptionList(GpsErrorCode.UNAUTHORIZED_GUARDIAN); //TODO: 이거 user로 해도 될거 같은데
        }

        User guardian = userRepository.getByUserId(loginUserId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GpsErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GpsErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, loginUserId);

        Gps gps = gpsRepository.getByZoneIdAndUserId(zoneId, userId);

        gps.updateSafeZone(
                request.title(),
                request.latitude(),
                request.longitude(),
                request.radiusMeter()
        );

        return GpsResponseDto.fromGps(gps);
    }

    public void delete(Long loginUserId, Long userId, Long zoneId) {
        if (loginUserId == null) {
            throw new ExceptionList(GpsErrorCode.UNAUTHORIZED_GUARDIAN); //TODO: 이거 user로 해도 될거 같은데
        }

        User guardian = userRepository.getByUserId(loginUserId);
        if (guardian.getRole() != Role.GUARDIAN) {
            throw new ExceptionList(GpsErrorCode.GUARDIAN_ONLY);
        }

        User user = userRepository.getByUserId(userId);
        if (user.getRole() != Role.USER) {
            throw new ExceptionList(GpsErrorCode.TARGET_USER_ONLY);
        }

        guardianLinkRepository.validateLinked(userId, loginUserId);

        Gps gps = gpsRepository.getByZoneIdAndUserId(zoneId, userId);

        gpsRepository.delete(gps);
    }
}
