package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외출 시작 가능 여부")
public record OutingStartResponse(
        @Schema(description = "외출 시작 가능 여부", example = "true")
        boolean canStart,

        @Schema(description = "외출 시작 불가 메시지", example = "저녁 8시 이후에는 외출 모드를 시작할 수 없어요.")
        String unavailableMessage
) {
}