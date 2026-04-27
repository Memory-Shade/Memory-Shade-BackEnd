package com.memoryshade.domain.game.controller;

import com.memoryshade.domain.game.dto.GameCreateResultRequestDto;
import com.memoryshade.domain.game.dto.GameCreateResultResponseDto;
import com.memoryshade.domain.game.dto.GameResponseDto;
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

@Tag(name = "Game", description = "게임 기록 API")
@SecurityRequirement(name = "bearerAuth")
public interface GameApi {

    @Operation(
            summary = "게임 결과 저장",
            description = "로그인 사용자의 게임 결과를 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "게임 결과 저장 성공",
                    content = @Content(schema = @Schema(implementation = GameCreateResultResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<GameCreateResultResponseDto> createGameResult(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Valid
            @RequestBody(
                    description = "게임 결과 저장 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GameCreateResultRequestDto.class))
            )
            GameCreateResultRequestDto request
    );

    @Operation(
            summary = "최고 기록 조회",
            description = "로그인 사용자의 최고 게임 기록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "최고 기록 조회 성공",
                    content = @Content(schema = @Schema(implementation = GameResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "게임 기록 없음")
    })
    ResponseEntity<GameResponseDto> getBestGame(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId
    );
}
