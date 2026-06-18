package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.KakaoUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// 카카오 API 베이스 URL 설정
@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    // 카카오 액세스 토큰을 헤더에 담아 내 정보 가져오기 API 호출
    @GetMapping("/v2/user/me")
    KakaoUserInfoResponse getUserInfo(@RequestHeader("Authorization") String accessToken);
}
