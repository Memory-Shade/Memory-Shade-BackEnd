package com.memoryshade.domain.guardianLink.controller;

import com.memoryshade.domain.guardianLink.dto.*;
import com.memoryshade.domain.guardianLink.service.GuardianLinkRequestService;
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
public class GuardianLinkRequestController {

    private final GuardianLinkRequestService guardianLinkRequestService;

    @PostMapping("/guardian-link-requests")
    public ResponseEntity<GuardianLinkRequestCreateResponseDto> createGuardianLinkRequest(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody GuardianLinkCreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guardianLinkRequestService.createGuardianLinkRequest(loginUserId, request));
    }

    @GetMapping("/guardian-link-requests/me")
    public ResponseEntity<List<GuardianLinkRequestGetResponseDto>> getMyGuardianLinkRequests(
            @AuthenticationPrincipal Long loginUserId
    ) {
        return ResponseEntity.ok(guardianLinkRequestService.getMyGuardianLinkRequests(loginUserId));
    }

    @PostMapping("/guardian-link-requests/{requestId}/accept")
    public ResponseEntity<GuardianLinkCreateResponseDto> acceptGuardianLinkRequest(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                guardianLinkRequestService.acceptGuardianLinkRequest(loginUserId, requestId)
        );
    }

    @PostMapping("/guardian-link-requests/{requestId}/reject")
    public ResponseEntity<GuardianLinkRequestRejectResponseDto> rejectGuardianLinkRequest(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(
                guardianLinkRequestService.rejectGuardianLinkRequest(loginUserId, requestId)
        );
    }
}
