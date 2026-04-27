package com.memoryshade.domain.gps.controller;

import com.memoryshade.domain.gps.dto.GpsRequestDto;
import com.memoryshade.domain.gps.dto.GpsResponseDto;
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

@Tag(name = "Gps", description = "안전 구역 관리 API")
@SecurityRequirement(name = "bearerAuth")
public interface GpsApi {

    @Operation(
            summary = "안전 구역 생성",
            description = "특정 사용자에 대한 안전 구역을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "안전 구역 생성 성공",
                    content = @Content(schema = @Schema(implementation = GpsResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "생성 권한 없음"),
            @ApiResponse(responseCode = "404", description = "대상 사용자 없음")
    })
    ResponseEntity<GpsResponseDto> create(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "안전 구역을 등록할 사용자 ID", example = "1", required = true)
            Long userId,

            @Valid
            @RequestBody(
                    description = "안전 구역 생성 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GpsRequestDto.class))
            )
            GpsRequestDto request
    );

    @Operation(
            summary = "안전 구역 목록 조회",
            description = "특정 사용자의 안전 구역 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "안전 구역 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GpsResponseDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "조회 권한 없음"),
            @ApiResponse(responseCode = "404", description = "대상 사용자 없음")
    })
    ResponseEntity<List<GpsResponseDto>> getSafeZones(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "안전 구역을 조회할 사용자 ID", example = "1", required = true)
            Long userId
    );

    @Operation(
            summary = "안전 구역 수정",
            description = "특정 사용자의 안전 구역 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "안전 구역 수정 성공",
                    content = @Content(schema = @Schema(implementation = GpsResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 안전 구역 없음")
    })
    ResponseEntity<GpsResponseDto> updateZps(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "안전 구역 소유 사용자 ID", example = "1", required = true)
            Long userId,

            @Parameter(description = "수정할 안전 구역 ID", example = "1", required = true)
            Long zoneId,

            @Valid
            @RequestBody(
                    description = "안전 구역 수정 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GpsRequestDto.class))
            )
            GpsRequestDto request
    );

    @Operation(
            summary = "안전 구역 삭제",
            description = "특정 사용자의 안전 구역을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "안전 구역 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 안전 구역 없음")
    })
    ResponseEntity<Void> deleteGps(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "안전 구역 소유 사용자 ID", example = "1", required = true)
            Long userId,

            @Parameter(description = "삭제할 안전 구역 ID", example = "1", required = true)
            Long zoneId
    );
}
