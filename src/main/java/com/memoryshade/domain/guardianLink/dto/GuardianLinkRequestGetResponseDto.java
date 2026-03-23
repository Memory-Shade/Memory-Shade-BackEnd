package com.memoryshade.domain.guardianLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequest;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequestStatus;

import java.time.LocalDateTime;

public record GuardianLinkRequestGetResponseDto(
        @JsonProperty("request_id")
        Long requestId,

        @JsonProperty("guardian_id")
        Long guardianId,

        @JsonProperty("guardian_name")
        String guardianName,

        @JsonProperty("guardian_phone_number")
        String guardianPhoneNumber,

        GuardianLinkRequestStatus status,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static GuardianLinkRequestGetResponseDto fromGuardianLinkRequest(GuardianLinkRequest request) {
        return new GuardianLinkRequestGetResponseDto(
                request.getRequestId(),
                request.getGuardian().getUserId(),
                request.getGuardian().getName(),
                request.getGuardian().getPhoneNumber(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }
}
