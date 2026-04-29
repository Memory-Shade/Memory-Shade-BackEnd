package com.memoryshade.domain.user.service;

import com.memoryshade.domain.auth.exception.AuthErrorCode;
import com.memoryshade.domain.user.dto.PushTokenUpdateRequestDto;
import com.memoryshade.domain.user.dto.PushTokenUpdateResponseDto;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public PushTokenUpdateResponseDto updateExpoPushToken(
            Long loginUserId,
            PushTokenUpdateRequestDto request
    ) {
        if (loginUserId == null) {
            throw new ExceptionList(AuthErrorCode.UNAUTHORIZED_USER);
        }

        User user = userRepository.getByUserId(loginUserId);
        user.updateFcmToken(request.expoPushToken());

        return new PushTokenUpdateResponseDto(user.getUserId(), user.getFcmToken());
    }
}
