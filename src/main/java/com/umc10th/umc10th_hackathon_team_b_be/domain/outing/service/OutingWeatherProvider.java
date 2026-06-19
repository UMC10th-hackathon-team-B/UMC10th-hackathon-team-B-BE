package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;

public interface OutingWeatherProvider {

    OutingFlowResponse.WeatherResponse getCurrentWeather(double latitude, double longitude);
}
