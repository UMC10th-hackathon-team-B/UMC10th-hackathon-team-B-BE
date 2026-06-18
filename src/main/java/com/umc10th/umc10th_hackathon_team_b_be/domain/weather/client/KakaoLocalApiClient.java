package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.KakaoRegionResponse;

@Component
public class KakaoLocalApiClient {

    private static final String KAKAO_AUTHORIZATION_PREFIX = "KakaoAK ";

    private final RestClient restClient;
    private final String restApiKey;

    public KakaoLocalApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${external.kakao.local-base-url}") String localBaseUrl,
            @Value("${external.kakao.rest-api-key}") String restApiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl(localBaseUrl)
                .build();
        this.restApiKey = restApiKey;
    }

    public String getRegionName(double latitude, double longitude) {
        KakaoRegionResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/geo/coord2regioncode.json")
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .build())
                .header("Authorization", KAKAO_AUTHORIZATION_PREFIX + restApiKey)
                .retrieve()
                .body(KakaoRegionResponse.class);

        if (response == null || response.documents() == null || response.documents().isEmpty()) {
            return "위치 정보 없음";
        }

        return response.documents().stream()
                .filter(document -> "H".equals(document.regionType()))
                .findFirst()
                .or(() -> response.documents().stream().findFirst())
                .map(this::formatRegionName)
                .orElse("위치 정보 없음");
    }

    private String formatRegionName(KakaoRegionResponse.Document document) {
        String region2DepthName = document.region2DepthName();
        String region3DepthName = document.region3DepthName();

        if (region2DepthName == null || region2DepthName.isBlank()) {
            return region3DepthName;
        }

        if (region3DepthName == null || region3DepthName.isBlank()) {
            return region2DepthName;
        }

        return region2DepthName + " " + region3DepthName;
    }
}