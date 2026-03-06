package com.memoryshade.domain.user.dto;

import com.memoryshade.domain.user.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequestDto(

        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(regexp = "^01[0-9]{8,9}$", message = "전화번호 형식이 올바르지 않습니다")
        String phoneNumber,

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다")
        String password,

        @NotBlank(message = "이름을 입력해주세요")
        String name,

        Role role
) {}
