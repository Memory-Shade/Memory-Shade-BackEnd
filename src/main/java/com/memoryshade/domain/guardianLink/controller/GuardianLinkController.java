package com.memoryshade.domain.guardianLink.controller;

import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateRequestDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateResponseDto;
import com.memoryshade.domain.guardianLink.service.GuardianLinkService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GuardianLinkController {

    private final GuardianLinkService guardianLinkService;

    @PostMapping("/{userId}/guardian-relations")
    public ResponseEntity<GuardianLinkCreateResponseDto> createGuardianRelation(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @Valid @RequestBody GuardianLinkCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guardianLinkService.createGuardianRelation(loginUserId, userId, request));
    }
}
