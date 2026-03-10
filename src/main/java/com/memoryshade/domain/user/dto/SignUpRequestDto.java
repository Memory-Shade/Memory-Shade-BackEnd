package com.memoryshade.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SignUpRequestDto(

        @JsonProperty("phone_number")
        @NotBlank(message = "전화번호를 입력해주세요")
        @Pattern(regexp = "^01[0-9]{8,9}$", message = "전화번호 형식이 올바르지 않습니다")
        String phoneNumber,

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다")
        String password,

        @NotBlank(message = "이름을 입력해주세요")
        String name,

        Role role
) {
        public User toUser(PasswordEncoder encoder) {
                return User.builder()
                        .phoneNumber(phoneNumber)
                        .password(encoder.encode(password))
                        .name(name)
                        .role(role)
                        .createdAt(LocalDateTime.now())
                        .build();
        }
}
