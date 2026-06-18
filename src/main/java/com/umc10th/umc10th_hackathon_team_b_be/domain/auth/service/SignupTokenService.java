package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service;

import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SignupTokenService {

    private static final String SIGNUP_TOKEN_PURPOSE_CLAIM = "purpose";
    private static final String SIGNUP_TOKEN_PURPOSE_VALUE = "SIGNUP";
    private static final String KAKAO_ID_CLAIM = "kakaoId";

    private final SecretKey signingKey;
    private final int signupTokenExpirationSeconds;

    public SignupTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.signup-token-expiration-seconds}") int signupTokenExpirationSeconds
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.signupTokenExpirationSeconds = signupTokenExpirationSeconds;
    }

    public IssuedSignupToken issue(String kakaoId) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + signupTokenExpirationSeconds * 1000L);

        String token = Jwts.builder()
                .claim(SIGNUP_TOKEN_PURPOSE_CLAIM, SIGNUP_TOKEN_PURPOSE_VALUE)
                .claim(KAKAO_ID_CLAIM, kakaoId)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey)
                .compact();

        return new IssuedSignupToken(token, signupTokenExpirationSeconds);
    }

    public String extractKakaoId(String signupToken) {
        if (!StringUtils.hasText(signupToken)) {
            throw new BusinessException(ErrorCode.AUTH_400);
        }

        Claims claims = parseClaims(signupToken);
        String purpose = claims.get(SIGNUP_TOKEN_PURPOSE_CLAIM, String.class);
        String kakaoId = claims.get(KAKAO_ID_CLAIM, String.class);

        if (!SIGNUP_TOKEN_PURPOSE_VALUE.equals(purpose) || !StringUtils.hasText(kakaoId)) {
            throw new BusinessException(ErrorCode.AUTH_400);
        }

        return kakaoId;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.AUTH_400);
        }
    }

    @Getter
    public static class IssuedSignupToken {
        private final String token;
        private final Integer expiresInSeconds;

        public IssuedSignupToken(String token, Integer expiresInSeconds) {
            this.token = token;
            this.expiresInSeconds = expiresInSeconds;
        }
    }
}
