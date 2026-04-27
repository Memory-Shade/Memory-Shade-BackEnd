package com.memoryshade.domain.notification.controller;

import com.memoryshade.domain.notification.dto.NotificationCreateRequestDto;
import com.memoryshade.domain.notification.dto.NotificationResponseDto;
import com.memoryshade.domain.notification.dto.NotificationUpdateReadRequestDto;
import com.memoryshade.domain.notification.model.NotiType;
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

@Tag(name = "Notification", description = "알림 관리 API")
@SecurityRequirement(name = "bearerAuth")
public interface NotificationApi {

    @Operation(
            summary = "알림 생성",
            description = "로그인 사용자에게 새로운 알림을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "알림 생성 성공",
                    content = @Content(schema = @Schema(implementation = NotificationResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<NotificationResponseDto> create(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Valid
            @RequestBody(
                    description = "알림 생성 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NotificationCreateRequestDto.class))
            )
            NotificationCreateRequestDto request
    );

    @Operation(
            summary = "내 알림 목록 조회",
            description = "로그인 사용자의 알림 목록을 조회합니다. 알림 타입과 읽음 여부로 필터링할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDto.class)))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(
                    description = "알림 타입 필터",
                    example = "GUARDIAN_REQUEST",
                    schema = @Schema(implementation = NotiType.class)
            )
            NotiType type,

            @Parameter(description = "읽음 여부 필터", example = "false")
            Boolean isRead
    );

    @Operation(
            summary = "알림 읽음 상태 수정",
            description = "특정 알림의 읽음 여부를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 읽음 상태 수정 성공",
                    content = @Content(schema = @Schema(implementation = NotificationResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    ResponseEntity<NotificationResponseDto> updateRead(
            @Parameter(hidden = true)
            @AuthenticationPrincipal
            Long loginUserId,

            @Parameter(description = "읽음 상태를 수정할 알림 ID", example = "1", required = true)
            Long notificationId,

            @Valid
            @RequestBody(
                    description = "알림 읽음 상태 수정 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NotificationUpdateReadRequestDto.class))
            )
            NotificationUpdateReadRequestDto request
    );
}
