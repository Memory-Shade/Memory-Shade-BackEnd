package com.memoryshade.domain.guardianLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.guardianLink.model.GuardianLinkRequestStatus;

public record GuardianLinkRequestRejectResponseDto(
        @JsonProperty("request_id")
        Long requestId,

        GuardianLinkRequestStatus status
) {
    public static GuardianLinkRequestRejectResponseDto from(Long requestId, GuardianLinkRequestStatus status) {
        return new GuardianLinkRequestRejectResponseDto(requestId, status);
    }
}
