package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.IssuedAuthTokens;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.entity.RefreshToken;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.repository.RefreshTokenRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.global.security.JwtTokenProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenIssueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public IssuedAuthTokens issueTokens(User user) {
        JwtTokenProvider.IssuedToken accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        JwtTokenProvider.IssuedToken refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken.token()))
                .expiresAt(refreshToken.expiresAt())
                .build());

        return IssuedAuthTokens.builder()
                .tokenType(jwtTokenProvider.getTokenType())
                .accessToken(accessToken.token())
                .accessTokenExpiresInSeconds((int) accessToken.expiresInSeconds())
                .refreshToken(refreshToken.token())
                .refreshTokenExpiresAt(refreshToken.expiresAtIsoText())
                .build();
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : hashed) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
