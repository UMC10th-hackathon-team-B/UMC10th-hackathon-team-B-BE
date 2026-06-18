package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.SunscreenAppliedOption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외출 세션 시작 요청")
public record OutingSessionCreateRequest(

        @Schema(description = "외출 시작 전 마지막 차단제 사용 시점", example = "BEFORE_30_MINUTES")
        @NotNull(message = "자외선 차단제 선택값은 필수입니다.")
        SunscreenAppliedOption sunscreenAppliedOption,

        @Schema(description = "현재 위치 위도", example = "37.5172")
        @NotNull(message = "위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        Double latitude,

        @Schema(description = "현재 위치 경도", example = "127.0473")
        @NotNull(message = "경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        Double longitude
) {
}
