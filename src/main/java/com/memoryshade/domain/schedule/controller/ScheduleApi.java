package com.memoryshade.domain.schedule.controller;

import com.memoryshade.domain.schedule.dto.ScheduleRequestDto;
import com.memoryshade.domain.schedule.dto.ScheduleResponseDto;
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

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Schedule", description = "일정 관리 API")
@SecurityRequirement(name = "bearerAuth")
public interface ScheduleApi {

    @Operation(
            summary = "일정 생성",
            description = "로그인 사용자의 일정을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "일정 생성 성공",
                    content = @Content(schema = @Schema(implementation = ScheduleResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<ScheduleResponseDto> createSchedule(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Valid
            @RequestBody(
                    description = "일정 생성 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleRequestDto.class))
            )
            ScheduleRequestDto request
    );

    @Operation(
            summary = "내 일정 조회",
            description = "특정 날짜의 로그인 사용자 일정 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduleResponseDto.class)))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 날짜 형식"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<List<ScheduleResponseDto>> getSchedules(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "조회할 날짜", example = "2026-03-24", required = true)
            LocalDate date
    );

    @Operation(
            summary = "일정 수정",
            description = "특정 일정의 제목과 알림 시간을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 수정 성공",
                    content = @Content(schema = @Schema(implementation = ScheduleResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정 없음")
    })
    ResponseEntity<ScheduleResponseDto> updateSchedule(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "수정할 일정 ID", example = "1", required = true)
            Long scheduleId,

            @Valid
            @RequestBody(
                    description = "일정 수정 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleRequestDto.class))
            )
            ScheduleRequestDto request
    );

    @Operation(
            summary = "일정 삭제",
            description = "특정 일정을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "일정 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정 없음")
    })
    ResponseEntity<Void> deleteSchedule(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long userId,

            @Parameter(description = "삭제할 일정 ID", example = "1", required = true)
            Long scheduleId
    );
}
