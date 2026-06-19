package com.umc10th.umc10th_hackathon_team_b_be.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE = "Bearer";
    private static final String USER_ID_CLAIM = "userId";
    private static final String TOKEN_PURPOSE_CLAIM = "tokenPurpose";
    private static final String ACCESS_TOKEN_PURPOSE = "ACCESS";
    private static final String REFRESH_TOKEN_PURPOSE = "REFRESH";
    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final SecretKey signingKey;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;
    private final Clock clock;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
            @Value("${jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds,
            Clock clock
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
        this.clock = clock;
    }

    public IssuedToken generateAccessToken(Long userId) {
        return generateToken(userId, ACCESS_TOKEN_PURPOSE, accessTokenExpirationSeconds);
    }

    public IssuedToken generateRefreshToken(Long userId) {
        return generateToken(userId, REFRESH_TOKEN_PURPOSE, refreshTokenExpirationSeconds);
    }

    public String getTokenType() {
        return TOKEN_TYPE;
    }

    public Long extractAccessTokenUserId(String token) {
        return extractUserId(token, ACCESS_TOKEN_PURPOSE);
    }

    public Long extractRefreshTokenUserId(String token) {
        return extractUserId(token, REFRESH_TOKEN_PURPOSE);
    }

    private Long extractUserId(String token, String expectedTokenPurpose) {
        var claims = Jwts.parser()
                .clock(() -> Date.from(clock.instant()))
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tokenPurpose = claims.get(TOKEN_PURPOSE_CLAIM, String.class);
        if (!expectedTokenPurpose.equals(tokenPurpose)) {
            throw new IllegalArgumentException("Invalid token purpose");
        }

        Object userIdClaim = claims.get(USER_ID_CLAIM);
        if (userIdClaim instanceof Number numberClaim) {
            return numberClaim.longValue();
        }
        return Long.parseLong(String.valueOf(userIdClaim));
    }

    private IssuedToken generateToken(Long userId, String tokenPurpose, long expiresInSeconds) {
        Instant issuedAtInstant = clock.instant();
        Instant expiresAtInstant = issuedAtInstant.plusSeconds(expiresInSeconds);
        Date issuedAt = Date.from(issuedAtInstant);
        Date expiresAtDate = Date.from(expiresAtInstant);

        String token = Jwts.builder()
                .claim(USER_ID_CLAIM, userId)
                .claim(TOKEN_PURPOSE_CLAIM, tokenPurpose)
                .issuedAt(issuedAt)
                .expiration(expiresAtDate)
                .signWith(signingKey)
                .compact();

        LocalDateTime expiresAt = LocalDateTime.ofInstant(expiresAtDate.toInstant(), SEOUL_ZONE_ID).withNano(0);

        return new IssuedToken(token, expiresInSeconds, expiresAt);
    }

    public record IssuedToken(
            String token,
            long expiresInSeconds,
            LocalDateTime expiresAt
    ) {
        public String expiresAtIsoText() {
            return expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}