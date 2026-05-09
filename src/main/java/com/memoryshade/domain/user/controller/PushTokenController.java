package com.memoryshade.domain.user.controller;

import com.memoryshade.domain.user.dto.PushTokenUpdateRequestDto;
import com.memoryshade.domain.user.dto.PushTokenUpdateResponseDto;
import com.memoryshade.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push-tokens")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PushTokenController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<PushTokenUpdateResponseDto> createExpoPushToken(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody PushTokenUpdateRequestDto request
    ) {
        return ResponseEntity.ok(userService.updateExpoPushToken(loginUserId, request));
    }
}
