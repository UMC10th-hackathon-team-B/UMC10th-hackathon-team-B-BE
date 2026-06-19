package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.WeatherObservationResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Weather", description = "홈 모드 날씨, 자외선, 계란 상태 조회 API")
public interface WeatherObservationControllerDocs {

    @Operation(
            summary = "홈 모드 날씨 및 자외선 정보 조회",
            description = "위도와 경도를 기반으로 법정동 위치명, 현재 날씨, 자외선 지수, 홈 모드 계란 상태, 외출 시작 가능 여부, 읽지 않은 알림 개수를 조회합니다. app-launches 없이 홈 화면에 먼저 진입한 경우 일일 자외선 알림 생성 조건도 함께 검사합니다.",
            operationId = "getWeatherObservation"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "홈 화면에 필요한 날씨/자외선/계란/알림 요약 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Access Token이 없거나 유효하지 않은 경우"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "날씨 또는 위치 외부 API 조회에 실패한 경우")
    })
    ResponseEntity<ApiResponse<WeatherObservationResponse>> getWeatherObservation(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @Parameter(description = "현재 위치 위도", example = "37.5172") @RequestParam double latitude,
            @Parameter(description = "현재 위치 경도", example = "127.0473") @RequestParam double longitude
    );
}
