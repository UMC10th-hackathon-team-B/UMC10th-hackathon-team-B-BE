package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssuedAuthTokens {

    private String tokenType;
    private String accessToken;
    private Integer accessTokenExpiresInSeconds;
    private String refreshToken;
    private String refreshTokenExpiresAt;
}
