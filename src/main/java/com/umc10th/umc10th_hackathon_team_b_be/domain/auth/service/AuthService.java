package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    // private final KakaoApiClient kakaoApiClient;
    // private final UserRepository userRepository;
    // private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthSessionResponse processKakaoLogin(AuthSessionRequest request) {

        // 1. 카카오 API를 호출하여 kakaoAccessToken으로 사용자 식별 (Kakao ID 추출)
        // String kakaoId = kakaoApiClient.getKakaoUserId(request.getKakaoAccessToken());

        // 2. DB에서 해당 카카오 ID의 가입 여부 확인
        // Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);

        // 3. 분기 처리
        // if (userOptional.isPresent()) {
        //     return handleExistingUser(userOptional.get()); // HOME 응답 객체 생성 및 JWT 발급
        // } else {
        //     return handleNewUser(kakaoId); // TERMS 응답 객체 생성 및 signupToken 발급
        // }

        return null; // TODO: 실제 로직 구현
    }
}