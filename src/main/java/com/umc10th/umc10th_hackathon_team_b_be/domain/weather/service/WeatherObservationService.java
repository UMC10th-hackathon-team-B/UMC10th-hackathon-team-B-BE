package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.service;

import java.time.LocalTime;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client.VWorldGeocoderClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service.NotificationService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.EggInfoResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.OutingStartResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.WeatherInfoResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.WeatherNotificationResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.WeatherObservationResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.EggStatus;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.UvLevel;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client.OpenWeatherApiClient;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.OpenWeatherResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherObservationService {

    private static final LocalTime OUTING_START_TIME = LocalTime.of(5, 0);
    private static final LocalTime OUTING_END_TIME = LocalTime.of(20, 0);

    private final NotificationService notificationService;
    private final VWorldGeocoderClient vWorldGeocoderClient;
    private final OpenWeatherApiClient openWeatherApiClient;

    @Transactional(readOnly = true)
    public WeatherObservationResponse getWeatherObservation(Long userId, double latitude, double longitude) {
        String locationName = vWorldGeocoderClient.getLocationName(latitude, longitude);

        OpenWeatherResult weatherResult = openWeatherApiClient.getCurrentWeather(latitude, longitude);

        double uvIndex = weatherResult.uvIndex();
        UvLevel uvLevel = resolveUvLevel(uvIndex);

        WeatherInfoResponse weather = new WeatherInfoResponse(
                locationName,
                weatherResult.weatherType(),
                weatherResult.weatherLabel(),
                weatherResult.temperatureCelsius(),
                uvIndex,
                uvLevel,
                resolveUvLevelLabel(uvLevel)
        );

        EggInfoResponse egg = new EggInfoResponse(
                EggStatus.SAFE,
                "안전한 계란",
                "오늘의 자외선을 확인해볼까요?"
        );

        OutingStartResponse outingStart = createOutingStartResponse();

        WeatherNotificationResponse notification = new WeatherNotificationResponse(
                notificationService.getUnreadCount(userId)
        );

        return new WeatherObservationResponse(
                weather,
                egg,
                outingStart,
                notification
        );
    }

    private OutingStartResponse createOutingStartResponse() {
        LocalTime now = LocalTime.now();

        boolean canStart = !now.isBefore(OUTING_START_TIME)
                && now.isBefore(OUTING_END_TIME);

        if (canStart) {
            return new OutingStartResponse(true, null);
        }

        return new OutingStartResponse(
                false,
                "현재 시간에는 외출 모드를 시작할 수 없어요."
        );
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