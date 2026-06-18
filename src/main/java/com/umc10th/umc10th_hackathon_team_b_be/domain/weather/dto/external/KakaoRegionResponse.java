package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoRegionResponse(
        List<Document> documents
) {

    public record Document(
            @JsonProperty("region_type")
            String regionType,

            @JsonProperty("region_1depth_name")
            String region1DepthName,

            @JsonProperty("region_2depth_name")
            String region2DepthName,

            @JsonProperty("region_3depth_name")
            String region3DepthName
    ) {
    }
}