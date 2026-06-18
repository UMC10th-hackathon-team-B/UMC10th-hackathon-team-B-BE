package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 목록 응답")
public record NotificationListResponse(

        @Schema(description = "읽지 않은 알림 개수", example = "2")
        long unreadCount,

        @Schema(description = "읽지 않은 알림 목록")
        List<NotificationItemResponse> notifications,

        @Schema(description = "알림이 없을 때 표시할 문구", example = "아직 확인할 알림이 없어요.")
        String emptyMessage
) {

    public static NotificationListResponse of(List<NotificationItemResponse> notifications) {
        long unreadCount = notifications.size();

        String emptyMessage = notifications.isEmpty()
                ? "아직 확인할 알림이 없어요."
                : null;

        return new NotificationListResponse(
                unreadCount,
                notifications,
                emptyMessage
        );
    }
}