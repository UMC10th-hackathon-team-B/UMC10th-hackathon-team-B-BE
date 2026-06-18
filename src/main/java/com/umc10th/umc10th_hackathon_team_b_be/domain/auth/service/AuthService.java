package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.IssuedAuthTokens;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.KakaoUserInfoResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;

import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;
    private final AuthTokenIssueService authTokenIssueService;
    private final SignupTokenService signupTokenService;

    @Transactional
    public AuthSessionResponse processKakaoLogin(AuthSessionRequest request) {

        // 1. 카카오 API 호출 시 Header에 "Bearer " 접두사 필수
        String authorizationHeader = "Bearer " + request.getKakaoAccessToken();

        // 카카오 API를 호출하여 kakaoAccessToken으로 사용자 식별 (Kakao ID 추출)
        KakaoUserInfoResponse kakaoUser;
        try {
            // 카카오 서버 호출
            kakaoUser = kakaoApiClient.getUserInfo(authorizationHeader);
        } catch (FeignException e) {
            // 카카오 토큰이 유효하지 않은 경우 명세서 기준 AUTH_400 반환
            throw new BusinessException(ErrorCode.AUTH_400);
        }

        String kakaoId = String.valueOf(kakaoUser.getId());

        // 2. DB에서 해당 카카오 ID의 가입 여부 확인
        Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);

        // 3. 기존 가입자 (HOME 이동)
        if (userOptional.isPresent()) {
            return handleExistingUser(userOptional.get()); // HOME 응답 객체 생성 및 JWT 발급
        }
        // 4. 신규 사용자 (TERMS 이동)
        else {
            return handleNewUser(kakaoId); // TERMS 응답 객체 생성 및 signupToken 발급
        }
    }

    private AuthSessionResponse handleExistingUser(User user) {
        IssuedAuthTokens issuedAuthTokens = authTokenIssueService.issueTokens(user);
        AuthSessionResponse.AuthTokenInfo authTokenInfo = AuthSessionResponse.AuthTokenInfo.builder()
                .tokenType(issuedAuthTokens.getTokenType())
                .accessToken(issuedAuthTokens.getAccessToken())
                .accessTokenExpiresInSeconds(issuedAuthTokens.getAccessTokenExpiresInSeconds())
                .refreshToken(issuedAuthTokens.getRefreshToken())
                .refreshTokenExpiresAt(issuedAuthTokens.getRefreshTokenExpiresAt())
                .build();

        return AuthSessionResponse.builder()
                .nextScreen("HOME")
                .userId(user.getId())
                .auth(authTokenInfo)
                .build();
    }

    private AuthSessionResponse handleNewUser(String kakaoId) {
        SignupTokenService.IssuedSignupToken issuedSignupToken = signupTokenService.issue(kakaoId);

        return AuthSessionResponse.builder()
                .nextScreen("TERMS")
                .signupToken(issuedSignupToken.getToken())
                .signupTokenExpiresInSeconds(issuedSignupToken.getExpiresInSeconds())
                .build();
    }
}