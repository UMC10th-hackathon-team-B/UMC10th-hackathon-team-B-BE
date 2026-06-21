package com.umc10th.umc10th_hackathon_team_b_be.domain.weather.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;
import com.umc10th.umc10th_hackathon_team_b_be.global.security.JwtTokenProvider;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WeatherObservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void invalidLatitudeRangeReturnsCommon400() throws Exception {
        User user = createUser();

        mockMvc.perform(get("/api/v1/weather-observations")
                        .header("Authorization", bearer(user))
                        .param("latitude", "91.0")
                        .param("longitude", "127.0473"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON_400"));
    }

    @Test
    void invalidLongitudeRangeReturnsCommon400() throws Exception {
        User user = createUser();

        mockMvc.perform(get("/api/v1/weather-observations")
                        .header("Authorization", bearer(user))
                        .param("latitude", "37.5172")
                        .param("longitude", "181.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON_400"));
    }

    @Test
    void missingLatitudeReturnsCommon400() throws Exception {
        User user = createUser();

        mockMvc.perform(get("/api/v1/weather-observations")
                        .header("Authorization", bearer(user))
                        .param("longitude", "127.0473"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON_400"));
    }

    @Test
    void invalidLatitudeTypeReturnsCommon400() throws Exception {
        User user = createUser();

        mockMvc.perform(get("/api/v1/weather-observations")
                        .header("Authorization", bearer(user))
                        .param("latitude", "invalid")
                        .param("longitude", "127.0473"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON_400"));
    }

    private User createUser() {
        return userRepository.save(User.builder()
                .kakaoId("weather-controller-test-" + UUID.randomUUID())
                .build());
    }

    private String bearer(User user) {
        return "Bearer " + jwtTokenProvider.generateAccessToken(user.getId()).token();
    }
}
