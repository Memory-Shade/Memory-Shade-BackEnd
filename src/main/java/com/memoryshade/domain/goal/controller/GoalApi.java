package com.memoryshade.domain.goal.controller;

import com.memoryshade.domain.goal.dto.GoalCreateRequestDto;
import com.memoryshade.domain.goal.dto.GoalCreateResponseDto;
import com.memoryshade.domain.goal.dto.GoalGetResponseDto;
import com.memoryshade.domain.goal.dto.GoalProgressResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Goal", description = "목표 관리 API")
@SecurityRequirement(name = "bearerAuth")
public interface GoalApi {

    @Operation(
            summary = "목표 생성",
            description = "로그인 사용자의 목표를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "목표 생성 성공",
                    content = @Content(schema = @Schema(implementation = GoalCreateResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<GoalCreateResponseDto> createGoal(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Valid
            @RequestBody(
                    description = "목표 생성 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GoalCreateRequestDto.class))
            )
            GoalCreateRequestDto request
    );

    @Operation(
            summary = "내 목표 조회",
            description = "로그인 사용자의 현재 목표를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "목표 조회 성공",
                    content = @Content(schema = @Schema(implementation = GoalGetResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "목표 없음")
    })
    ResponseEntity<GoalGetResponseDto> getMeGoal(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId
    );

    @Operation(
            summary = "목표 진행률 조회",
            description = "로그인 사용자의 목표 진행률을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "진행률 조회 성공",
                    content = @Content(schema = @Schema(implementation = GoalProgressResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "목표 없음")
    })
    ResponseEntity<GoalProgressResponseDto> getGoalProgress(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId
    );
}
