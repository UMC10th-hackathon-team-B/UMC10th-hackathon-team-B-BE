package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenWeatherResponse(
        Current current
) {

    public record Current(
            double temp,

            double uvi,

            List<Weather> weather
    ) {
    }

    public record Weather(
            long id,

            String main,

            String description,

            String icon
    ) {
    }
}