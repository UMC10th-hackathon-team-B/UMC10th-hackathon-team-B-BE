package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.UvLevel;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.WeatherType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 모드 날씨 및 자외선 정보")
public record WeatherInfoResponse(
        @Schema(description = "위치명", example = "송파구 잠실6동")
        String locationName,

        @Schema(description = "날씨 타입", example = "CLEAR")
        WeatherType weatherType,

        @Schema(description = "날씨 표시명", example = "맑음")
        String weatherLabel,

        @Schema(description = "섭씨 온도", example = "24.6")
        double temperatureCelsius,

        @Schema(description = "자외선 지수", example = "7.2")
        double uvIndex,

        @Schema(description = "자외선 단계", example = "HIGH")
        UvLevel uvLevel,

        @Schema(description = "자외선 단계 표시명", example = "높음")
        String uvLevelLabel
) {
}