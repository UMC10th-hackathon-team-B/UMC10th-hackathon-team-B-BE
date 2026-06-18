package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.controller;

import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.controller.docs.NotificationControllerDocs;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationListResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationReadRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service.NotificationService;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(@CurrentUserId Long userId) {
        NotificationListResponse response = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PatchMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationListResponse>> readNotification(
            @CurrentUserId Long userId,
            @PathVariable Long notificationId,
            @Valid @RequestBody NotificationReadRequest request
    ) {
        NotificationListResponse response = notificationService.readNotification(userId, notificationId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}