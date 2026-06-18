package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.WeatherObservationResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Weather", description = "날씨 및 자외선 API")
public interface WeatherObservationControllerDocs {

    @Operation(
            summary = "홈 모드 날씨 및 자외선 정보 조회",
            description = "위도와 경도를 기반으로 홈 화면에 필요한 날씨, 자외선, 계란 상태, 외출 시작 가능 여부, 읽지 않은 알림 개수를 조회합니다.",
            operationId = "getWeatherObservation"
    )
    ResponseEntity<ApiResponse<WeatherObservationResponse>> getWeatherObservation(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @RequestParam double latitude,
            @RequestParam double longitude
    );
}