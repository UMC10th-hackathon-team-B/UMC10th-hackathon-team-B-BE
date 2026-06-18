package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.controller.docs.WeatherObservationControllerDocs;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.WeatherObservationResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.service.WeatherObservationService;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather-observations")
public class WeatherObservationController implements WeatherObservationControllerDocs {

    // TODO: 인증 필터/ArgumentResolver 적용 후 현재 로그인 사용자 ID로 교체
    private static final Long TEMP_USER_ID = 1L;

    private final WeatherObservationService weatherObservationService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<WeatherObservationResponse>> getWeatherObservation(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        WeatherObservationResponse response = weatherObservationService.getWeatherObservation(
                TEMP_USER_ID,
                latitude,
                longitude
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}