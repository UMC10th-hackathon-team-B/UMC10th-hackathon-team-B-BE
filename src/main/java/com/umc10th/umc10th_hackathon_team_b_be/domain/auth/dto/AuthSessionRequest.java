package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthSessionRequest {

    @Schema(description = "카카오 OAuth 인증 후 발급받은 access token", example = "kakao-access-token-example")
    @NotBlank(message = "카카오 액세스 토큰은 필수입니다.")
    private String kakaoAccessToken;
}