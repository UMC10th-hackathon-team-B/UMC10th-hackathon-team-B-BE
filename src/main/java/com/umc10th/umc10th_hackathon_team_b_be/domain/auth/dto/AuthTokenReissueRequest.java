package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthTokenReissueRequest {

    @Schema(description = "Access Token 재발급에 사용할 refresh token", example = "refresh-token-example")
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}
