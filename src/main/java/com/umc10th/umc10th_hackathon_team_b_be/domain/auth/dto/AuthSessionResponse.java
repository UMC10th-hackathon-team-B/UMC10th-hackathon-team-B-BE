package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthSessionResponse {

    @Schema(description = "로그인 후 이동할 화면. HOME, TERMS", example = "HOME")
    private String nextScreen;

    @Schema(description = "기존 가입 사용자 ID (신규 가입 시 null)", example = "1", nullable = true)
    private Long userId;

    @Schema(description = "기존 가입자에게 발급되는 인증 토큰 정보 (신규 가입 시 null)", nullable = true)
    private AuthTokenInfo auth;

    @Schema(description = "신규 사용자 약관 동의 및 가입에 사용할 임시 토큰 (기존 가입자 시 null)", example = "signup-token-example", nullable = true)
    private String signupToken;

    @Schema(description = "가입 토큰 만료까지 남은 초 (기존 가입자 시 null)", example = "600", nullable = true)
    private Integer signupTokenExpiresInSeconds;

    @Getter
    @Builder
    public static class AuthTokenInfo {
        @Schema(description = "인증 헤더에 사용할 토큰 타입", example = "Bearer")
        private String tokenType;

        @Schema(description = "API 인증에 사용할 access token", example = "access-token-example")
        private String accessToken;

        @Schema(description = "Access Token 만료까지 남은 초", example = "1800")
        private Integer accessTokenExpiresInSeconds;

        @Schema(description = "자동 로그인 유지와 토큰 재발급에 사용할 refresh token", example = "refresh-token-example")
        private String refreshToken;

        @Schema(description = "Refresh Token 만료 일시", example = "2026-07-18T09:00:00")
        private String refreshTokenExpiresAt;
    }
}