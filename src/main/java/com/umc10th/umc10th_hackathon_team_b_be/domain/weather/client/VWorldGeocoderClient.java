package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.VWorldGeocoderResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class VWorldGeocoderClient {

    private static final String OK_STATUS = "OK";

    private final RestClient restClient;

    @Value("${external.vworld.base-url}")
    private String geocoderUrl;

    @Value("${external.vworld.api-key}")
    private String apiKey;

    public String getLocationName(double latitude, double longitude) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(geocoderUrl)
                .queryParam("service", "address")
                .queryParam("request", "getAddress")
                .queryParam("version", "2.0")
                .queryParam("crs", "epsg:4326")
                .queryParam("point", longitude + "," + latitude)
                .queryParam("format", "json")
                .queryParam("type", "both")
                .queryParam("zipcode", "false")
                .queryParam("simple", "false")
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        VWorldGeocoderResponse response;

        try {
            response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(VWorldGeocoderResponse.class);
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        return parseLocationName(response);
    }

    private String parseLocationName(VWorldGeocoderResponse response) {
        if (response == null || response.response() == null) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        if (!OK_STATUS.equalsIgnoreCase(response.response().status())) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        if (response.response().result() == null || response.response().result().isEmpty()) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        VWorldGeocoderResponse.Structure structure = response.response()
                .result()
                .get(0)
                .structure();

        if (structure == null) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        String district = firstNonBlank(structure.level3(), structure.level2());
        String legalDong = structure.level4L();

        if (isBlank(district) || isBlank(legalDong)) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        return district + " " + legalDong;
    }

    private String firstNonBlank(String first, String second) {
        if (!isBlank(first)) {
            return first;
        }

        return second;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}