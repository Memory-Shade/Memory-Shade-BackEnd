package com.memoryshade.domain.guardianLink.controller;

import com.memoryshade.domain.guardianLink.dto.GuardianLinkGetResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "GuardianLink", description = "보호자 연결 조회 및 해제 API")
@SecurityRequirement(name = "bearerAuth")
public interface GuardianLinkApi {

    @Operation(
            summary = "내 연결 사용자 목록 조회",
            description = "로그인한 보호자에게 연결된 사용자 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "연결 사용자 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuardianLinkGetResponseDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<List<GuardianLinkGetResponseDto>> getAllLinkUsersMe(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId
    );

    @Operation(
            summary = "내 연결 보호자 목록 조회",
            description = "로그인한 사용자에게 연결된 보호자 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "연결 보호자 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuardianLinkGetResponseDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<List<GuardianLinkGetResponseDto>> getAllLinkGuardiansMe(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId
    );

    @Operation(
            summary = "보호자 연결 해제",
            description = "특정 사용자와의 보호자 연결을 해제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "보호자 연결 해제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "해제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "연결 대상 사용자 또는 연결 정보 없음")
    })
    ResponseEntity<Void> deleteGuardianLink(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "연결 해제할 사용자 ID", example = "1", required = true)
            Long userId
    );
}
