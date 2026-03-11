package com.memoryshade.domain.notification.controller;

import com.memoryshade.domain.notification.dto.NotificationCreateRequestDto;
import com.memoryshade.domain.notification.dto.NotificationResponseDto;
import com.memoryshade.domain.notification.dto.NotificationUpdateReadRequestDto;
import com.memoryshade.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody NotificationCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(loginUserId, request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal Long loginUserId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(loginUserId));
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDto> updateRead(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long notificationId,
            @Valid @RequestBody NotificationUpdateReadRequestDto request) {
        return ResponseEntity.ok(notificationService.updateRead(loginUserId, notificationId, request));
    }
}
