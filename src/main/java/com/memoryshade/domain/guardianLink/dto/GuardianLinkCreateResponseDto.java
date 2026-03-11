package com.memoryshade.domain.guardianLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.guardianLink.model.GuardianLink;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GuardianLinkCreateResponseDto(

        @JsonProperty("link_id")
        Long linkId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("guardian_id")
        Long guardianId
) {
    public static GuardianLinkCreateResponseDto fromGuardian(GuardianLink guardianLink) {
        return new GuardianLinkCreateResponseDto(
                guardianLink.getLinkId(),
                guardianLink.getUser().getUserId(),
                guardianLink.getGuardian().getUserId()
        );
    }
}
