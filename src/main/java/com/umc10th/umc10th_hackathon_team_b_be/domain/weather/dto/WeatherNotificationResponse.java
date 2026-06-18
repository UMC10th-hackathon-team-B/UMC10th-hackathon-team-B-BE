package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 요약 정보")
public record WeatherNotificationResponse(
        @Schema(description = "읽지 않은 알림 개수", example = "2")
        long unreadCount
) {
}