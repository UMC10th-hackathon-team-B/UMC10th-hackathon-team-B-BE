package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VWorldGeocoderResponse(
        Response response
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            String status,
            List<Result> result
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            String type,
            String text,
            Structure structure
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Structure(
            String level2,
            String level3,
            String level4L,
            String level4A
    ) {
    }
}