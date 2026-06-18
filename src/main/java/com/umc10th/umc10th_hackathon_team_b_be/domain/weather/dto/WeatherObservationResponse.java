package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 모드 날씨 및 자외선 조회 응답")
public record WeatherObservationResponse(
        @Schema(description = "날씨 및 자외선 정보")
        WeatherInfoResponse weather,

        @Schema(description = "홈 모드 계란 정보")
        EggInfoResponse egg,

        @Schema(description = "외출 시작 가능 여부")
        OutingStartResponse outingStart,

        @Schema(description = "알림 정보")
        WeatherNotificationResponse notification
) {
}