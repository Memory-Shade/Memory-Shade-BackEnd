package com.memoryshade.domain.guardianLink.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.guardianLink.model.GuardianLink;
import com.memoryshade.domain.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GuardianLinkCreateRequestDto(

        @JsonProperty("phone_number")
        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(regexp = "^01[0-9]{8,9}$", message = "전화번호 형식이 올바르지 않습니다")
        String phoneNumber
) {
        public GuardianLink toGuardianLink(User user, User guardian) {
                return GuardianLink.builder()
                        .user(user)
                        .guardian(guardian)
                        .build();
        }
}
