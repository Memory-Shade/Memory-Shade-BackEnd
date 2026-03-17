package com.memoryshade.domain.guardianLink.controller;

import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateRequestDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateResponseDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkGetResponseDto;
import com.memoryshade.domain.guardianLink.service.GuardianLinkService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GuardianLinkController {

    private final GuardianLinkService guardianLinkService;

    @PostMapping("/guardian-links")
    public ResponseEntity<GuardianLinkCreateResponseDto> createGuardianLink(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody GuardianLinkCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guardianLinkService.createGuardianLink(loginUserId, request));
    }

    @GetMapping("/guardian-links/me/users")
    public ResponseEntity<List<GuardianLinkGetResponseDto>> getAllLinkUsersMe(
            @AuthenticationPrincipal Long loginUserId
    ) {
        return ResponseEntity.ok(guardianLinkService.getAllLinkUser(loginUserId));
    }

    @GetMapping("/guardian-links/me/guardians")
    public ResponseEntity<List<GuardianLinkGetResponseDto>> getAllLinkGuardiansMe(
            @AuthenticationPrincipal Long loginUserId
    ) {
        return ResponseEntity.ok(guardianLinkService.getAllLinkGuardian(loginUserId));
    }
}
