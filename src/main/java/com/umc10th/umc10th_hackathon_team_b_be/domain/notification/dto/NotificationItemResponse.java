package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.entity.Notification;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.enums.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 항목 응답")
public record NotificationItemResponse(

        @Schema(description = "알림 ID", example = "31")
        Long notificationId,

        @Schema(description = "알림 타입", example = "EGG_DANGER")
        NotificationType type,

        @Schema(description = "알림 제목", example = "자외선 차단제를 발라주세요!")
        String title,

        @Schema(description = "알림 내용", example = "계란이가 많이 익었어요. 자외선 노출에 주의해주세요.")
        String content,

        @Schema(description = "알림 생성 일시", example = "2026-06-18T10:25:00")
        LocalDateTime createdAt,

        @Schema(description = "화면 표시용 생성 시간", example = "오전 10:25")
        String createdTimeText,

        @Schema(description = "읽음 여부", example = "false")
        boolean isRead
) {

    public static NotificationItemResponse from(Notification notification) {
        return new NotificationItemResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getContent(),
                notification.getCreatedAt(),
                formatCreatedTime(notification.getCreatedAt()),
                notification.getIsRead()
        );
    }

    private static String formatCreatedTime(LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN);
        return createdAt.format(formatter);
    }
}