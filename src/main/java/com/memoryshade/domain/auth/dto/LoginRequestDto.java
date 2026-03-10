package com.memoryshade.domain.auth.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDto(

        @JsonProperty("phone_number")
        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(regexp = "^01[0-9]{8,9}$", message = "전화번호 형식이 올바르지 않습니다")
        String phoneNumber,

        @NotBlank(message = "비밀번호를 입력해주세요")
        String password
) {
}

