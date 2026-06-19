package com.umc10th.umc10th_hackathon_team_b_be.domain.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.KakaoUserInfoResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service.KakaoApiClient;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthUserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KakaoApiClient kakaoApiClient;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @BeforeEach
    void setUpKakaoMock() {
        when(kakaoApiClient.getUserInfo(anyString())).thenAnswer(invocation -> {
            String authorization = invocation.getArgument(0, String.class);
            String token = authorization.replace("Bearer ", "");
            long kakaoId = switch (token) {
                case "kakao-token-user-1" -> 1001L;
                case "kakao-token-user-2" -> 1002L;
                case "kakao-token-user-3" -> 1003L;
                case "kakao-token-user-4" -> 1004L;
                case "kakao-token-user-5" -> 1005L;
                case "kakao-token-user-6" -> 1006L;
                case "kakao-token-user-7" -> 1007L;
                case "kakao-token-user-8" -> 1008L;
                case "kakao-token-user-9" -> 1009L;
                default -> 9999L;
            };
            return kakaoUserInfo(kakaoId);
        });
    }

    @Test
    void loginNewUserReturnsTermsAndSignupToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"kakaoAccessToken":"kakao-token-user-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nextScreen").value("TERMS"))
                .andExpect(jsonPath("$.data.signupToken").isNotEmpty())
                .andExpect(jsonPath("$.data.signupTokenExpiresInSeconds").value(600));
    }

    @Test
    void signupWithInvalidSignupTokenReturnsAuth400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "signupToken":"invalid-signup-token",
                                  "agreedTermTypes":["SERVICE","PRIVACY","LOCATION"]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_400"));
    }

    @Test
    void signupThenLoginExistingUserReturnsHomeWithAuthTokens() throws Exception {
        Map<String, Object> firstLoginData = login("kakao-token-user-5");
        String signupToken = String.valueOf(firstLoginData.get("signupToken"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "signupToken", signupToken,
                                "agreedTermTypes", List.of("SERVICE", "PRIVACY", "LOCATION")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("HOME"))
                .andExpect(jsonPath("$.data.auth.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.auth.refreshToken").isNotEmpty());

        waitForNextTokenSecond();

        mockMvc.perform(post("/api/v1/auth-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"kakaoAccessToken":"kakao-token-user-5"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextScreen").value("HOME"))
                .andExpect(jsonPath("$.data.auth.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.auth.refreshToken").isNotEmpty());
    }

    @Test
    void reusedRefreshTokenReturnsAuth401() throws Exception {
        Map<String, String> tokens = signupAndGetTokens("kakao-token-user-3");
        String refreshToken = tokens.get("refreshToken");

        waitForNextTokenSecond();

        mockMvc.perform(post("/api/v1/auth-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());

        mockMvc.perform(post("/api/v1/auth-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_401"));
    }

    @Test
    void tamperedRefreshTokenReturnsAuth401() throws Exception {
        Map<String, String> tokens = signupAndGetTokens("kakao-token-user-4");
        String tamperedRefreshToken = tokens.get("refreshToken") + "tampered";

        mockMvc.perform(post("/api/v1/auth-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", tamperedRefreshToken))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_401"));
    }

    @Test
    void expiredRefreshTokenReturnsAuth401() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .claim("userId", 12345L)
                .claim("tokenPurpose", "REFRESH")
                .issuedAt(java.util.Date.from(Instant.now().minusSeconds(3600)))
                .expiration(java.util.Date.from(Instant.now().minusSeconds(1800)))
                .signWith(key)
                .compact();

        mockMvc.perform(post("/api/v1/auth-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", expiredToken))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_401"));
    }

    @Test
    void logoutRevokesRefreshToken() throws Exception {
        Map<String, String> tokens = signupAndGetTokens("kakao-token-user-6");

        mockMvc.perform(delete("/api/v1/auth-sessions/current")
                        .header("Authorization", "Bearer " + tokens.get("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", tokens.get("refreshToken")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());

        mockMvc.perform(post("/api/v1/auth-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", tokens.get("refreshToken")))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_401"));
    }

    @Test
    void accessTokenCanAccessProtectedApi() throws Exception {
        Map<String, String> tokens = signupAndGetTokens("kakao-token-user-7");

        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + tokens.get("accessToken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void refreshTokenCannotAccessProtectedApi() throws Exception {
        Map<String, String> tokens = signupAndGetTokens("kakao-token-user-8");

        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + tokens.get("refreshToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_401"));
    }

    @Test
    void accessTokenCannotReissueAuthTokens() throws Exception {
        Map<String, String> tokens = signupAndGetTokens("kakao-token-user-9");

        mockMvc.perform(post("/api/v1/auth-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", tokens.get("accessToken")))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_401"));
    }

    @Test
    void legacyTokenWithoutPurposeReturnsAuth401() throws Exception {
        String legacyToken = legacyTokenWithoutPurpose(12345L);

        mockMvc.perform(post("/api/v1/auth-tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", legacyToken))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_401"));
    }
    private String legacyTokenWithoutPurpose(Long userId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(java.util.Date.from(Instant.now()))
                .expiration(java.util.Date.from(Instant.now().plusSeconds(1800)))
                .signWith(key)
                .compact();
    }
    private Map<String, String> signupAndGetTokens(String kakaoAccessToken) throws Exception {
        Map<String, Object> loginData = login(kakaoAccessToken);
        String signupToken = String.valueOf(loginData.get("signupToken"));

        MvcResult signupResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "signupToken", signupToken,
                                "agreedTermTypes", List.of("SERVICE", "PRIVACY", "LOCATION")
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> body = objectMapper.readValue(
                signupResult.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        Map<String, Object> auth = (Map<String, Object>) data.get("auth");

        assertThat(auth.get("accessToken")).isNotNull();
        assertThat(auth.get("refreshToken")).isNotNull();

        return Map.of(
                "accessToken", String.valueOf(auth.get("accessToken")),
                "refreshToken", String.valueOf(auth.get("refreshToken"))
        );
    }

    private Map<String, Object> login(String kakaoAccessToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("kakaoAccessToken", kakaoAccessToken))))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> body = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );
        return (Map<String, Object>) body.get("data");
    }

    private KakaoUserInfoResponse kakaoUserInfo(long id) {
        KakaoUserInfoResponse response = new KakaoUserInfoResponse();
        ReflectionTestUtils.setField(response, "id", id);
        return response;
    }

    private void waitForNextTokenSecond() throws InterruptedException {
        Thread.sleep(1100);
    }
}
