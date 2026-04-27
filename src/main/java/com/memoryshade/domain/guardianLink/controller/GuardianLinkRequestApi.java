package com.memoryshade.domain.guardianLink.controller;

import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateRequestDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkCreateResponseDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkRequestCreateResponseDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkRequestGetResponseDto;
import com.memoryshade.domain.guardianLink.dto.GuardianLinkRequestRejectResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "GuardianLinkRequest", description = "보호자 연결 요청 API")
@SecurityRequirement(name = "bearerAuth")
public interface GuardianLinkRequestApi {

    @Operation(
            summary = "보호자 연결 요청 생성",
            description = "전화번호를 기반으로 보호자 연결 요청을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "보호자 연결 요청 생성 성공",
                    content = @Content(schema = @Schema(implementation = GuardianLinkRequestCreateResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "대상 보호자 없음"),
            @ApiResponse(responseCode = "409", description = "이미 요청이 존재하거나 이미 연결된 관계")
    })
    ResponseEntity<GuardianLinkRequestCreateResponseDto> createGuardianLinkRequest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Valid
            @RequestBody(
                    description = "보호자 연결 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GuardianLinkCreateRequestDto.class))
            )
            GuardianLinkCreateRequestDto request
    );

    @Operation(
            summary = "내 보호자 연결 요청 목록 조회",
            description = "로그인 사용자가 받은 보호자 연결 요청 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "보호자 연결 요청 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GuardianLinkRequestGetResponseDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<List<GuardianLinkRequestGetResponseDto>> getMyGuardianLinkRequests(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId
    );

    @Operation(
            summary = "보호자 연결 요청 수락",
            description = "특정 보호자 연결 요청을 수락하고 실제 연결을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "보호자 연결 요청 수락 성공",
                    content = @Content(schema = @Schema(implementation = GuardianLinkCreateResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "수락 권한 없음"),
            @ApiResponse(responseCode = "404", description = "요청 없음"),
            @ApiResponse(responseCode = "409", description = "이미 처리된 요청 또는 이미 연결된 관계")
    })
    ResponseEntity<GuardianLinkCreateResponseDto> acceptGuardianLinkRequest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "수락할 요청 ID", example = "1", required = true)
            Long requestId
    );

    @Operation(
            summary = "보호자 연결 요청 거절",
            description = "특정 보호자 연결 요청을 거절합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "보호자 연결 요청 거절 성공",
                    content = @Content(schema = @Schema(implementation = GuardianLinkRequestRejectResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "거절 권한 없음"),
            @ApiResponse(responseCode = "404", description = "요청 없음"),
            @ApiResponse(responseCode = "409", description = "이미 처리된 요청")
    })
    ResponseEntity<GuardianLinkRequestRejectResponseDto> rejectGuardianLinkRequest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "거절할 요청 ID", example = "1", required = true)
            Long requestId
    );
}
