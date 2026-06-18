package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.OpenWeatherResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.OpenWeatherResult;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.WeatherType;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;

@Component
public class OpenWeatherApiClient {

    private final RestClient restClient;
    private final String apiKey;

    public OpenWeatherApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${external.open-weather.base-url}") String baseUrl,
            @Value("${external.open-weather.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }

    public OpenWeatherResult getCurrentWeather(double latitude, double longitude) {
        OpenWeatherResponse response;

        try {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/3.0/onecall")
                            .queryParam("lat", latitude)
                            .queryParam("lon", longitude)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "kr")
                            .queryParam("exclude", "minutely,hourly,daily,alerts")
                            .build())
                    .retrieve()
                    .body(OpenWeatherResponse.class);
        } catch (RestClientException exception) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        if (response == null || response.current() == null) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }

        OpenWeatherResponse.Current current = response.current();

        String main = extractWeatherMain(current);
        WeatherType weatherType = mapWeatherType(main);

        return new OpenWeatherResult(
                weatherType,
                resolveWeatherLabel(weatherType),
                current.temp(),
                current.uvi()
        );
    }

    private String extractWeatherMain(OpenWeatherResponse.Current current) {
        if (current.weather() == null || current.weather().isEmpty()) {
            return "Clear";
        }

        return current.weather().get(0).main();
    }

    private WeatherType mapWeatherType(String main) {
        if (main == null || main.isBlank()) {
            return WeatherType.CLEAR;
        }

        return switch (main.toUpperCase()) {
            case "THUNDERSTORM" -> WeatherType.THUNDERSTORM;
            case "DRIZZLE" -> WeatherType.DRIZZLE;
            case "RAIN" -> WeatherType.RAIN;
            case "SNOW" -> WeatherType.SNOW;
            case "MIST" -> WeatherType.MIST;
            case "SMOKE" -> WeatherType.SMOKE;
            case "HAZE" -> WeatherType.HAZE;
            case "DUST" -> WeatherType.DUST;
            case "FOG" -> WeatherType.FOG;
            case "SAND" -> WeatherType.SAND;
            case "ASH" -> WeatherType.ASH;
            case "SQUALL" -> WeatherType.SQUALL;
            case "TORNADO" -> WeatherType.TORNADO;
            case "CLOUDS" -> WeatherType.CLOUDS;
            case "CLEAR" -> WeatherType.CLEAR;
            default -> WeatherType.CLEAR;
        };
    }

    private String resolveWeatherLabel(WeatherType weatherType) {
        return switch (weatherType) {
            case THUNDERSTORM -> "뇌우";
            case DRIZZLE -> "이슬비";
            case RAIN -> "비";
            case SNOW -> "눈";
            case MIST -> "옅은 안개";
            case SMOKE -> "연기";
            case HAZE -> "실안개";
            case DUST -> "먼지";
            case FOG -> "안개";
            case SAND -> "모래먼지";
            case ASH -> "화산재";
            case SQUALL -> "돌풍";
            case TORNADO -> "토네이도";
            case CLEAR -> "맑음";
            case CLOUDS -> "구름";
        };
    }
}
