package com.memoryshade.domain.gps.controller;

import com.memoryshade.domain.gps.dto.GpsCreateRequestDto;
import com.memoryshade.domain.gps.dto.GpsCreateResponseDto;
import com.memoryshade.domain.gps.service.GpsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class GpsController {

    private final GpsService gpsService;

    @PostMapping("/{userId}/safe-zones")
    public ResponseEntity<GpsCreateResponseDto> create(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @Valid @RequestBody GpsCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gpsService.Create(loginUserId, userId, request));
    }
}
