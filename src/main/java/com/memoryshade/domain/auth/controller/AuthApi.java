package com.memoryshade.domain.auth.controller;

import com.memoryshade.domain.auth.dto.AuthResponseDto;
import com.memoryshade.domain.auth.dto.LoginRequestDto;
import com.memoryshade.domain.user.dto.SignUpRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthApi {
    @Operation(
            summary = "회원가입",
            description = "사용자 정보를 입력받아 회원가입을 진행하고 access token을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 사용자")
    })
    @SecurityRequirements
    ResponseEntity<AuthResponseDto> signUp(
            @Valid
            @RequestBody(
                    description = "회원가입 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequestDto.class))
            )
            SignUpRequestDto request
    );

    @Operation(
            summary = "로그인",
            description = "전화번호와 비밀번호로 로그인하고 access token을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @SecurityRequirements
    ResponseEntity<AuthResponseDto> login(
            @Valid
            @RequestBody(
                    description = "로그인 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
            )
            LoginRequestDto request
    );

    @Operation(
            summary = "로그아웃",
            description = "현재 로그인된 사용자의 토큰 또는 세션 정보를 무효화합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> logout(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId
    );
}
