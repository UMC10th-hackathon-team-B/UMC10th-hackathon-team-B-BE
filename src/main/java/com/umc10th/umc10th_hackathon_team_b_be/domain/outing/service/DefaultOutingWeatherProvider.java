package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client.OpenWeatherApiClient;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client.VWorldGeocoderClient;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.OpenWeatherResult;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.UvLevel;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultOutingWeatherProvider implements OutingWeatherProvider {

    private final VWorldGeocoderClient vWorldGeocoderClient;
    private final OpenWeatherApiClient openWeatherApiClient;

    @Override
    public OutingFlowResponse.WeatherResponse getCurrentWeather(double latitude, double longitude) {
        String locationName = vWorldGeocoderClient.getLocationName(latitude, longitude);
        OpenWeatherResult weatherResult = openWeatherApiClient.getCurrentWeather(latitude, longitude);
        UvLevel uvLevel = resolveUvLevel(weatherResult.uvIndex());

        return new OutingFlowResponse.WeatherResponse(
                locationName,
                weatherResult.weatherType().name(),
                weatherResult.weatherLabel(),
                roundToTwoDecimalPlaces(weatherResult.temperatureCelsius()),
                roundToTwoDecimalPlaces(weatherResult.uvIndex()),
                uvLevel.name(),
                resolveUvLevelLabel(uvLevel)
        );
    }

    private BigDecimal roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private UvLevel resolveUvLevel(double uvIndex) {
        if (uvIndex <= 2) {
            return UvLevel.LOW;
        }

        if (uvIndex <= 5) {
            return UvLevel.NORMAL;
        }

        if (uvIndex <= 7) {
            return UvLevel.HIGH;
        }

        if (uvIndex <= 10) {
            return UvLevel.VERY_HIGH;
        }

        return UvLevel.DANGER;
    }

    private String resolveUvLevelLabel(UvLevel uvLevel) {
        return switch (uvLevel) {
            case LOW -> "낮음";
            case NORMAL -> "보통";
            case HIGH -> "높음";
            case VERY_HIGH -> "매우 높음";
            case DANGER -> "위험";
        };
    }
}
