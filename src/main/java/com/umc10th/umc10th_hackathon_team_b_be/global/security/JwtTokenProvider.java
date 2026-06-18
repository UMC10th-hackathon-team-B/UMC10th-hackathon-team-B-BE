package com.umc10th.umc10th_hackathon_team_b_be.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
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
    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final SecretKey signingKey;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
            @Value("${jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

    public IssuedToken generateAccessToken(Long userId) {
        return generateToken(userId, accessTokenExpirationSeconds);
    }

    public IssuedToken generateRefreshToken(Long userId) {
        return generateToken(userId, refreshTokenExpirationSeconds);
    }

    public String getTokenType() {
        return TOKEN_TYPE;
    }

    private IssuedToken generateToken(Long userId, long expiresInSeconds) {
        Date issuedAt = new Date();
        Date expiresAtDate = new Date(issuedAt.getTime() + expiresInSeconds * 1000L);

        String token = Jwts.builder()
                .claim(USER_ID_CLAIM, userId)
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
