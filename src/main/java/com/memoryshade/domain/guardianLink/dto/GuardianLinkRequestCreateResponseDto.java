package com.memoryshade.domain.guardianLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequest;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequestStatus;

import java.time.LocalDateTime;

public record GuardianLinkRequestCreateResponseDto(
        @JsonProperty("request_id")
        Long requestId,

        @JsonProperty("guardian_id")
        Long guardianId,

        @JsonProperty("user_id")
        Long userId,

        GuardianLinkRequestStatus status,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static GuardianLinkRequestCreateResponseDto fromGuardianLinkRequest(GuardianLinkRequest request) {
        return new GuardianLinkRequestCreateResponseDto(
                request.getRequestId(),
                request.getGuardian().getUserId(),
                request.getUser().getUserId(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }
}
