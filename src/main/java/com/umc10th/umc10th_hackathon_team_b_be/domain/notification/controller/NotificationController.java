package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.controller;

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
    public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications() {
        Long userId = 1L; // TODO: 인증 구현 후 현재 로그인 사용자 ID로 교체

        NotificationListResponse response = notificationService.getUnreadNotifications(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PatchMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationListResponse>> readNotification(
            @PathVariable Long notificationId,
            @Valid @RequestBody NotificationReadRequest request
    ) {
        Long userId = 1L; // TODO: 인증 구현 후 현재 로그인 사용자 ID로 교체

        NotificationListResponse response = notificationService.readNotification(userId, notificationId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}