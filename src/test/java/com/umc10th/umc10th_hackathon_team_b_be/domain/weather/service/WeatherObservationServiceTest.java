package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Stream;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service.NotificationService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client.OpenWeatherApiClient;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client.VWorldGeocoderClient;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.WeatherObservationResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.OpenWeatherResult;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.UvLevel;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.WeatherType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherObservationServiceTest {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Long USER_ID = 1L;
    private static final double LATITUDE = 37.5172;
    private static final double LONGITUDE = 127.0473;

    @Mock
    private NotificationService notificationService;

    @Mock
    private VWorldGeocoderClient vWorldGeocoderClient;

    @Mock
    private OpenWeatherApiClient openWeatherApiClient;

    @ParameterizedTest
    @MethodSource("weatherObservationTimes")
    void getWeatherObservationReturnsHomeWeatherAndOutingStart(
            String instant,
            boolean expectedCanStart,
            String expectedUnavailableMessage
    ) {
        WeatherObservationService service = new WeatherObservationService(
                notificationService,
                vWorldGeocoderClient,
                openWeatherApiClient,
                fixedClock(instant)
        );
        OpenWeatherResult weatherResult = new OpenWeatherResult(
                WeatherType.CLEAR,
                "맑음",
                24.6,
                7.0
        );

        when(vWorldGeocoderClient.getLocationName(LATITUDE, LONGITUDE)).thenReturn("송파구 문정동");
        when(openWeatherApiClient.getCurrentWeather(LATITUDE, LONGITUDE)).thenReturn(weatherResult);
        when(notificationService.getUnreadCount(USER_ID)).thenReturn(3L);

        WeatherObservationResponse response = service.getWeatherObservation(USER_ID, LATITUDE, LONGITUDE);

        assertThat(response.weather().locationName()).isEqualTo("송파구 문정동");
        assertThat(response.weather().weatherType()).isEqualTo(WeatherType.CLEAR);
        assertThat(response.weather().weatherLabel()).isEqualTo("맑음");
        assertThat(response.weather().temperatureCelsius()).isEqualTo(24.6);
        assertThat(response.weather().uvIndex()).isEqualTo(7.0);
        assertThat(response.weather().uvLevel()).isEqualTo(UvLevel.HIGH);
        assertThat(response.weather().uvLevelLabel()).isEqualTo("높음");
        assertThat(response.egg().eggStatusLabel()).isEqualTo("안전한 계란");
        assertThat(response.egg().message()).isEqualTo("오늘의 자외선을 확인해볼까요?");
        assertThat(response.outingStart().canStart()).isEqualTo(expectedCanStart);
        assertThat(response.outingStart().unavailableMessage()).isEqualTo(expectedUnavailableMessage);
        assertThat(response.notification().unreadCount()).isEqualTo(3L);

        InOrder inOrder = inOrder(notificationService);
        inOrder.verify(notificationService).createDailyUvIfNeeded(USER_ID, 7.0);
        inOrder.verify(notificationService).getUnreadCount(USER_ID);
    }

    private static Stream<Arguments> weatherObservationTimes() {
        return Stream.of(
                Arguments.of("2026-06-18T20:00:00Z", true, null),
                Arguments.of("2026-06-19T10:59:00Z", true, null),
                Arguments.of("2026-06-19T11:00:00Z", false, "저녁 8시 이후에는 외출 모드를 시작할 수 없어요."),
                Arguments.of("2026-06-18T19:59:00Z", false, "저녁 8시 이후에는 외출 모드를 시작할 수 없어요.")
        );
    }

    private static Clock fixedClock(String instant) {
        return Clock.fixed(Instant.parse(instant), ZONE_ID);
    }
}
