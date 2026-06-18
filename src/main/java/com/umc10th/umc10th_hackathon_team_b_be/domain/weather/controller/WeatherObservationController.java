package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.controller;

import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;
import io.swagger.v3.oas.annotations.Parameter;
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

    private final WeatherObservationService weatherObservationService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<WeatherObservationResponse>> getWeatherObservation(
            @Parameter(hidden = true) @CurrentUserId Long userId,
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        WeatherObservationResponse response = weatherObservationService.getWeatherObservation(
                userId,
                latitude,
                longitude
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}