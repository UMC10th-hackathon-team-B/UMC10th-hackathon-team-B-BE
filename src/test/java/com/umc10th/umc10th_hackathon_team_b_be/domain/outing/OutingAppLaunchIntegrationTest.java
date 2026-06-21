package com.umc10th.umc10th_hackathon_team_b_be.domain.outing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.IssuedAuthTokens;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service.AuthTokenIssueService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.repository.NotificationRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.entity.OutingSession;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionEndReason;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionStatus;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.repository.OutingSessionRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.service.OutingWeatherProvider;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;
import com.umc10th.umc10th_hackathon_team_b_be.global.security.JwtTokenProvider;

@SpringBootTest(properties = "jwt.secret=outing-test-secret-key-at-least-32-bytes")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OutingAppLaunchIntegrationTest {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OutingSessionRepository outingSessionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthTokenIssueService authTokenIssueService;

    @MockBean
    private Clock clock;

    @MockBean
    private OutingWeatherProvider outingWeatherProvider;

    @BeforeEach
    void setUp() {
        setNow(LocalDateTime.of(2026, 6, 18, 9, 0));
        when(outingWeatherProvider.getCurrentWeather(anyDouble(), anyDouble()))
                .thenReturn(weather("7.20"));
    }

    @Test
    void appLaunchWithoutCurrentSessionReturnsHome() throws Exception {
        User user = createUser();

        mockMvc.perform(post("/api/v1/app-launches")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("HOME"))
                .andExpect(jsonPath("$.data.home.weather.uvIndex").value(7.20))
                .andExpect(jsonPath("$.data.home.egg.eggStatus").value("SAFE"))
                .andExpect(jsonPath("$.data.home.outingStart.canStart").value(true));
    }

    @Test
    void createOutingSessionStoresSunscreenAndUvSnapshot() throws Exception {
        User user = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 9, 10));

        mockMvc.perform(post("/api/v1/outing-sessions")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sunscreenAppliedOption": "BEFORE_30_MINUTES",
                                  "latitude": 37.5172,
                                  "longitude": 127.0473
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("OUTING"))
                .andExpect(jsonPath("$.data.outing.outingSession.elapsedMinutes").value(0))
                .andExpect(jsonPath("$.data.outing.weather.uvIndex").value(7.20))
                .andExpect(jsonPath("$.data.outing.egg.eggStatus").value("SAFE"))
                .andExpect(jsonPath("$.data.outing.sunscreen.lastSunscreenAppliedText")
                        .value("30분 전 마지막 기록"));

        OutingSession session = currentSession(user);
        assertThat(session.getAutoEndAt()).isEqualTo(LocalDateTime.of(2026, 6, 18, 20, 0));
        assertThat(session.getStartUvIndex()).isEqualByComparingTo("7.20");
        assertThat(session.getLastSunscreenAppliedAt()).isEqualTo(LocalDateTime.of(2026, 6, 18, 8, 40));
    }

    @Test
    void createOutingSessionCalculatesEggBySunscreenRisk() throws Exception {
        assertCreateOutingSessionEggStatus("BEFORE_5_MINUTES", "SAFE");
        assertCreateOutingSessionEggStatus("BEFORE_15_MINUTES", "SAFE");
        assertCreateOutingSessionEggStatus("BEFORE_30_MINUTES", "SAFE");
        assertCreateOutingSessionEggStatus("BEFORE_60_MINUTES", "LIGHT_TOASTED");
        assertCreateOutingSessionEggStatus("BEFORE_120_MINUTES", "TOASTED");
        assertCreateOutingSessionEggStatus("NONE", "DANGER");
    }

    @Test
    void createOutingSessionUsesSeoulTimeWhenClockZoneIsNotSeoul() throws Exception {
        User user = createUser();
        setClockInstantAtSeoulTime(LocalDateTime.of(2026, 6, 18, 9, 10), UTC_ZONE_ID);

        mockMvc.perform(post("/api/v1/outing-sessions")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSessionJson("NONE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("OUTING"))
                .andExpect(jsonPath("$.data.outing.outingSession.startedAt").value("2026-06-18T09:10:00"))
                .andExpect(jsonPath("$.data.outing.outingSession.autoEndAt").value("2026-06-18T20:00:00"));
    }

    @Test
    void duplicateOutingSessionReturnsOuting409() throws Exception {
        User user = createUser();
        createSession(user, "NONE");

        mockMvc.perform(post("/api/v1/outing-sessions")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSessionJson("NONE")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("OUTING_409"));
    }

    @Test
    void currentSessionRecalculatesEggAndCreatesEggDangerOnce() throws Exception {
        User user = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 9, 0));
        when(outingWeatherProvider.getCurrentWeather(anyDouble(), anyDouble()))
                .thenReturn(weather("8.00"));
        createSession(user, "BEFORE_5_MINUTES");

        setNow(LocalDateTime.of(2026, 6, 18, 12, 0));

        mockMvc.perform(get("/api/v1/outing-sessions/current")
                        .header("Authorization", bearer(user))
                        .param("latitude", "37.5172")
                        .param("longitude", "127.0473"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("OUTING"))
                .andExpect(jsonPath("$.data.outing.egg.eggStatus").value("DANGER"))
                .andExpect(jsonPath("$.data.outing.notification.unreadCount").value(1));

        mockMvc.perform(get("/api/v1/outing-sessions/current")
                        .header("Authorization", bearer(user))
                        .param("latitude", "37.5172")
                        .param("longitude", "127.0473"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.outing.notification.unreadCount").value(1));

        assertThat(notificationRepository.countByUser_IdAndIsReadFalse(user.getId())).isEqualTo(1);
    }

    @Test
    void sunscreenApplicationRecordsCurrentTimeAndRecalculates() throws Exception {
        User user = createUser();
        when(outingWeatherProvider.getCurrentWeather(anyDouble(), anyDouble()))
                .thenReturn(weather("8.00"));
        createSession(user, "NONE");

        setNow(LocalDateTime.of(2026, 6, 18, 9, 45));

        mockMvc.perform(post("/api/v1/outing-sessions/current/sunscreen-applications")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.outing.egg.eggStatus").value("SAFE"))
                .andExpect(jsonPath("$.data.outing.sunscreen.lastSunscreenAppliedText")
                        .value("방금 전 마지막 기록"));

        assertThat(currentSession(user).getLastSunscreenAppliedAt())
                .isEqualTo(LocalDateTime.of(2026, 6, 18, 9, 45));
    }

    @Test
    void manualCompleteEndsCurrentSession() throws Exception {
        User user = createUser();
        Long sessionId = createSession(user, "NONE");
        setNow(LocalDateTime.of(2026, 6, 18, 10, 20));

        mockMvc.perform(patch("/api/v1/outing-sessions/{outingSessionId}", sessionId)
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COMPLETED",
                                  "latitude": 37.5172,
                                  "longitude": 127.0473
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("HOME"))
                .andExpect(jsonPath("$.data.endedSession.endReason").value("MANUAL"))
                .andExpect(jsonPath("$.data.endedSession.endedAt").value("2026-06-18T10:20:00"));

        OutingSession session = outingSessionRepository.findById(sessionId).orElseThrow();
        assertThat(session.getStatus()).isEqualTo(OutingSessionStatus.COMPLETED);
        assertThat(session.getEndReason()).isEqualTo(OutingSessionEndReason.MANUAL);
    }

    @Test
    void autoEndTakesPriorityOverManualCompletePathId() throws Exception {
        User user = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 19, 0));
        Long sessionId = createSession(user, "NONE");
        setNow(LocalDateTime.of(2026, 6, 18, 20, 10));

        mockMvc.perform(patch("/api/v1/outing-sessions/{outingSessionId}", sessionId + 999)
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COMPLETED",
                                  "latitude": 37.5172,
                                  "longitude": 127.0473
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("HOME"))
                .andExpect(jsonPath("$.data.endedSession.endReason").value("AUTO"))
                .andExpect(jsonPath("$.data.endedSession.endedAt").value("2026-06-18T20:00:00"))
                .andExpect(jsonPath("$.data.autoEndNotice.showPopup").value(true));
    }

    @Test
    void autoEndNoticeIsHiddenAfterNextDayFive() throws Exception {
        User user = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 19, 0));
        createSession(user, "NONE");
        setNow(LocalDateTime.of(2026, 6, 19, 5, 0));

        mockMvc.perform(post("/api/v1/app-launches")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("HOME"))
                .andExpect(jsonPath("$.data.endedSession.endReason").value("AUTO"))
                .andExpect(jsonPath("$.data.autoEndNotice.showPopup").value(false));
    }

    @Test
    void logoutCompletesCurrentSessionWithLogoutOrAutoReason() throws Exception {
        User logoutUser = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 9, 0));
        Long logoutSessionId = createSession(logoutUser, "NONE");
        IssuedAuthTokens logoutTokens = authTokenIssueService.issueTokens(logoutUser);

        mockMvc.perform(delete("/api/v1/auth-sessions/current")
                        .header("Authorization", "Bearer " + logoutTokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "refreshToken", logoutTokens.getRefreshToken()
                        ))))
                .andExpect(status().isOk());

        assertThat(outingSessionRepository.findById(logoutSessionId).orElseThrow().getEndReason())
                .isEqualTo(OutingSessionEndReason.LOGOUT);

        User autoUser = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 19, 0));
        Long autoSessionId = createSession(autoUser, "NONE");
        setNow(LocalDateTime.of(2026, 6, 18, 20, 10));
        IssuedAuthTokens autoTokens = authTokenIssueService.issueTokens(autoUser);

        mockMvc.perform(delete("/api/v1/auth-sessions/current")
                        .header("Authorization", "Bearer " + autoTokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "refreshToken", autoTokens.getRefreshToken()
                        ))))
                .andExpect(status().isOk());

        OutingSession autoSession = outingSessionRepository.findById(autoSessionId).orElseThrow();
        assertThat(autoSession.getEndReason()).isEqualTo(OutingSessionEndReason.AUTO);
        assertThat(autoSession.getEndedAt()).isEqualTo(LocalDateTime.of(2026, 6, 18, 20, 0));
    }

    @Test
    void invalidRequestsReturnCommon400AndWeatherFailureReturnsWeather502() throws Exception {
        User user = createUser();

        mockMvc.perform(post("/api/v1/outing-sessions")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sunscreenAppliedOption": "BAD",
                                  "latitude": 37.5172,
                                  "longitude": 127.0473
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400"));

        mockMvc.perform(post("/api/v1/app-launches")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "latitude": 91.0,
                                  "longitude": 127.0473
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400"));

        mockMvc.perform(get("/api/v1/outing-sessions/current")
                        .header("Authorization", bearer(user))
                        .param("latitude", "37.5172"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400"));

        when(outingWeatherProvider.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(null);

        mockMvc.perform(post("/api/v1/app-launches")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson()))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("WEATHER_502"));
    }

    @Test
    void outingUnavailableTimeReturnsOuting400() throws Exception {
        User user = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 20, 0));

        mockMvc.perform(post("/api/v1/outing-sessions")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSessionJson("NONE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("OUTING_400"))
                .andExpect(jsonPath("$.message").value("저녁 8시 이후에는 외출 모드를 시작할 수 없어요."));
    }

    private User createUser() {
        return userRepository.save(User.builder()
                .kakaoId("outing-test-" + UUID.randomUUID())
                .build());
    }

    private void assertCreateOutingSessionEggStatus(
            String sunscreenAppliedOption,
            String expectedEggStatus
    ) throws Exception {
        User user = createUser();
        setNow(LocalDateTime.of(2026, 6, 18, 9, 10));
        when(outingWeatherProvider.getCurrentWeather(anyDouble(), anyDouble()))
                .thenReturn(weather("9.22"));

        mockMvc.perform(post("/api/v1/outing-sessions")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSessionJson(sunscreenAppliedOption)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.outing.egg.eggStatus").value(expectedEggStatus));
    }

    private Long createSession(User user, String sunscreenAppliedOption) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/outing-sessions")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSessionJson(sunscreenAppliedOption)))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> body = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        Map<String, Object> outing = (Map<String, Object>) data.get("outing");
        Map<String, Object> outingSession = (Map<String, Object>) outing.get("outingSession");
        return Long.valueOf(String.valueOf(outingSession.get("outingSessionId")));
    }

    private OutingSession currentSession(User user) {
        return outingSessionRepository.findFirstByUser_IdAndStatusOrderByStartedAtDesc(
                user.getId(),
                OutingSessionStatus.IN_PROGRESS
        ).orElseThrow();
    }

    private String bearer(User user) {
        return "Bearer " + jwtTokenProvider.generateAccessToken(user.getId()).token();
    }

    private void setNow(LocalDateTime now) {
        when(clock.getZone()).thenReturn(SEOUL_ZONE_ID);
        when(clock.instant()).thenReturn(now.atZone(SEOUL_ZONE_ID).toInstant());
    }

    private void setClockInstantAtSeoulTime(LocalDateTime seoulTime, ZoneId clockZone) {
        when(clock.getZone()).thenReturn(clockZone);
        when(clock.instant()).thenReturn(seoulTime.atZone(SEOUL_ZONE_ID).toInstant());
    }

    private String locationJson() {
        return """
                {
                  "latitude": 37.5172,
                  "longitude": 127.0473
                }
                """;
    }

    private String createSessionJson(String sunscreenAppliedOption) {
        return """
                {
                  "sunscreenAppliedOption": "%s",
                  "latitude": 37.5172,
                  "longitude": 127.0473
                }
                """.formatted(sunscreenAppliedOption);
    }

    private OutingFlowResponse.WeatherResponse weather(String uvIndex) {
        return new OutingFlowResponse.WeatherResponse(
                "송파구 문정동",
                "CLEAR",
                "맑음",
                new BigDecimal("24.6"),
                new BigDecimal(uvIndex),
                uvLevel(uvIndex),
                uvLevelLabel(uvIndex)
        );
    }

    private String uvLevel(String uvIndex) {
        BigDecimal value = new BigDecimal(uvIndex);
        if (value.compareTo(BigDecimal.valueOf(3)) < 0) {
            return "LOW";
        }
        if (value.compareTo(BigDecimal.valueOf(6)) < 0) {
            return "NORMAL";
        }
        if (value.compareTo(BigDecimal.valueOf(8)) < 0) {
            return "HIGH";
        }
        if (value.compareTo(BigDecimal.valueOf(11)) < 0) {
            return "VERY_HIGH";
        }
        return "DANGER";
    }

    private String uvLevelLabel(String uvIndex) {
        return switch (uvLevel(uvIndex)) {
            case "LOW" -> "낮음";
            case "NORMAL" -> "보통";
            case "HIGH" -> "높음";
            case "VERY_HIGH" -> "매우 높음";
            default -> "위험";
        };
    }
}
