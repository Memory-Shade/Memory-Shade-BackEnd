package com.memoryshade.domain.auth.service;

import com.memoryshade.domain.auth.dto.AuthResponseDto;
import com.memoryshade.domain.auth.dto.LoginRequestDto;
import com.memoryshade.domain.user.dto.SignUpRequestDto;
import com.memoryshade.domain.auth.exception.AuthErrorCode;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.config.JwtProvider;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponseDto signUp(SignUpRequestDto request) {
        userRepository.validateDuplicatePhoneNumber(request.phoneNumber());

        User user = request.toUser(passwordEncoder);
        userRepository.save(user);

        String token = jwtProvider.generateAccessToken(user.getUserId(), user.getPhoneNumber());
        return AuthResponseDto.of(user, token);
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.getByPhoneNumber(request.phoneNumber());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ExceptionList(AuthErrorCode.INVALID_PASSWORD);
        }

        user.updateLastLoggedAt();

        String token = jwtProvider.generateAccessToken(user.getUserId(), user.getPhoneNumber());
        return AuthResponseDto.of(user, token);
    }
}
