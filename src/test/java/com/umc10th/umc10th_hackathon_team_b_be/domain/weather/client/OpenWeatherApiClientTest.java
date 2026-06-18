package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.stream.Stream;

import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.dto.external.OpenWeatherResult;
import com.umc10th.umc10th_hackathon_team_b_be.domain.weather.enums.WeatherType;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OpenWeatherApiClientTest {

    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String API_KEY = "test-openweather-api-key";

    @ParameterizedTest
    @MethodSource("weatherLabels")
    void getCurrentWeatherMapsWeatherMain(String main, WeatherType expectedType, String expectedLabel) {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenWeatherApiClient client = new OpenWeatherApiClient(builder, BASE_URL, API_KEY);

        server.expect(requestTo(containsString("/data/3.0/onecall")))
                .andRespond(withSuccess("""
                        {
                          "current": {
                            "temp": 24.6,
                            "uvi": 7.2,
                            "weather": [
                              {
                                "id": 800,
                                "main": "%s",
                                "description": "description",
                                "icon": "01d"
                              }
                            ]
                          }
                        }
                        """.formatted(main), MediaType.APPLICATION_JSON));

        OpenWeatherResult result = client.getCurrentWeather(37.5172, 127.0473);

        assertThat(result.weatherType()).isEqualTo(expectedType);
        assertThat(result.weatherLabel()).isEqualTo(expectedLabel);
        assertThat(result.temperatureCelsius()).isEqualTo(24.6);
        assertThat(result.uvIndex()).isEqualTo(7.2);
        server.verify();
    }

    @Test
    void getCurrentWeatherThrowsWeather502WhenResponseHasNoCurrent() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenWeatherApiClient client = new OpenWeatherApiClient(builder, BASE_URL, API_KEY);

        server.expect(requestTo(containsString("/data/3.0/onecall")))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.getCurrentWeather(37.5172, 127.0473))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.WEATHER_502);
        server.verify();
    }

    @Test
    void getCurrentWeatherThrowsWeather502WhenOpenWeatherFails() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenWeatherApiClient client = new OpenWeatherApiClient(builder, BASE_URL, API_KEY);

        server.expect(requestTo(containsString("/data/3.0/onecall")))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getCurrentWeather(37.5172, 127.0473))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.WEATHER_502);
        server.verify();
    }

    private static Stream<Arguments> weatherLabels() {
        return Stream.of(
                Arguments.of("Thunderstorm", WeatherType.THUNDERSTORM, "뇌우"),
                Arguments.of("Drizzle", WeatherType.DRIZZLE, "이슬비"),
                Arguments.of("Rain", WeatherType.RAIN, "비"),
                Arguments.of("Snow", WeatherType.SNOW, "눈"),
                Arguments.of("Mist", WeatherType.MIST, "옅은 안개"),
                Arguments.of("Smoke", WeatherType.SMOKE, "연기"),
                Arguments.of("Haze", WeatherType.HAZE, "실안개"),
                Arguments.of("Dust", WeatherType.DUST, "먼지"),
                Arguments.of("Fog", WeatherType.FOG, "안개"),
                Arguments.of("Sand", WeatherType.SAND, "모래먼지"),
                Arguments.of("Ash", WeatherType.ASH, "화산재"),
                Arguments.of("Squall", WeatherType.SQUALL, "돌풍"),
                Arguments.of("Tornado", WeatherType.TORNADO, "토네이도"),
                Arguments.of("Clear", WeatherType.CLEAR, "맑음"),
                Arguments.of("Clouds", WeatherType.CLOUDS, "구름")
        );
    }
}
