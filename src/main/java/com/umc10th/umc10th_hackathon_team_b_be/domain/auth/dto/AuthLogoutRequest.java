package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthLogoutRequest {

    @Schema(description = "현재 로그인 세션에서 사용 중인 refresh token", example = "refresh-token-example")
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}
