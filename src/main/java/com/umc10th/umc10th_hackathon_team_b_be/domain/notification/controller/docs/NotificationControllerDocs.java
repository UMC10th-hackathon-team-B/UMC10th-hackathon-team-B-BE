package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationListResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationReadRequest;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Notification", description = "알림함 관련 API")
public interface NotificationControllerDocs {

    @Operation(
            summary = "알림함 조회",
            description = "읽지 않은 알림 목록을 최신순으로 조회합니다.",
            operationId = "getNotifications"
    )
    ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(
            summary = "알림 읽음 처리",
            description = "알림을 읽음 처리하고 남은 읽지 않은 알림 목록을 반환합니다.",
            operationId = "readNotification"
    )
    ResponseEntity<ApiResponse<NotificationListResponse>> readNotification(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @PathVariable Long notificationId,
            @Valid @RequestBody NotificationReadRequest request
    );
}