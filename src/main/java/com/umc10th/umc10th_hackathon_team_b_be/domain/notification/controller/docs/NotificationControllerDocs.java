package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationListResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationReadRequest;
import com.umc10th.umc10th_hackathon_team_b_be.global.config.SwaggerErrorExamples;
import com.umc10th.umc10th_hackathon_team_b_be.global.config.SwaggerSuccessExamples;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Notification", description = "알림함 조회 및 읽음 처리 API")
public interface NotificationControllerDocs {

    @Operation(
            summary = "알림함 조회",
            description = "현재 사용자의 읽지 않은 알림(isRead=false)만 최신순으로 조회합니다. 알림이 없으면 notifications는 빈 배열이고 emptyMessage에 화면 표시 문구가 담깁니다.",
            operationId = "getNotifications"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "읽지 않은 알림 목록과 unreadCount 반환",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "NOTIFICATION_LIST", value = SwaggerSuccessExamples.NOTIFICATION_LIST),
                                    @ExampleObject(name = "NOTIFICATION_EMPTY", value = SwaggerSuccessExamples.NOTIFICATION_EMPTY)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Access Token이 없거나 유효하지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            )
    })
    ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(
            summary = "알림 읽음 처리",
            description = "notificationId에 해당하는 알림을 읽음 처리한 뒤, 남아 있는 읽지 않은 알림 목록과 unreadCount를 다시 반환합니다. 요청 본문의 isRead는 true로 전달합니다.",
            operationId = "readNotification"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "읽음 처리 성공 및 갱신된 알림 목록 반환",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "NOTIFICATION_READ", value = SwaggerSuccessExamples.NOTIFICATION_READ),
                                    @ExampleObject(name = "NOTIFICATION_EMPTY", value = SwaggerSuccessExamples.NOTIFICATION_EMPTY)
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "알림 읽음 처리 요청값이 잘못된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Access Token이 없거나 유효하지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "알림이 없거나 현재 사용자 소유 알림이 아닌 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "NOTIFICATION_404", value = SwaggerErrorExamples.NOTIFICATION_404)
                    )
            )
    })
    ResponseEntity<ApiResponse<NotificationListResponse>> readNotification(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "읽음 처리할 알림 ID", example = "31") @PathVariable Long notificationId,
            @Valid @RequestBody NotificationReadRequest request
    );
}
