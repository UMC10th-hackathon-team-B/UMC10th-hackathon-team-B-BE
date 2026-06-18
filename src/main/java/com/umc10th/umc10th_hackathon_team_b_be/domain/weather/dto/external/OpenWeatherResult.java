package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.WeatherType;

public record OpenWeatherResult(
        WeatherType weatherType,
        String weatherLabel,
        double temperatureCelsius,
        double uvIndex
) {
}