package com.memoryshade.domain.gps.service;

import com.memoryshade.domain.gps.dto.GpsCreateRequestDto;
import com.memoryshade.domain.gps.dto.GpsCreateResponseDto;
import com.memoryshade.domain.gps.exception.GpsErrorCode;
import com.memoryshade.domain.gps.model.Gps;
import com.memoryshade.domain.gps.repository.GpsRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GpsService {

    private final GpsRepository gpsRepository;
    private final UserRepository userRepository;

    public GpsCreateResponseDto Create(Long loginUserId, Long userId, GpsCreateRequestDto request) {

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

        return GpsCreateResponseDto.fromGps(gps);
    }
}
