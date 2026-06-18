package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.EggStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 모드 계란 상태 정보")
public record EggInfoResponse(
        @Schema(description = "계란 상태", example = "SAFE")
        EggStatus eggStatus,

        @Schema(description = "계란 상태 표시명", example = "안전한 계란")
        String eggStatusLabel,

        @Schema(description = "홈 모드 화면 문구", example = "오늘의 자외선을 확인해볼까요?")
        String message
) {
}