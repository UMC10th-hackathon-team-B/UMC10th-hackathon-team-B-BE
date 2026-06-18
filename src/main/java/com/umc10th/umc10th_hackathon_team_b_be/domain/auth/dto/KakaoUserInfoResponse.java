package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponse {

    // 카카오 시스템에서 발급하는 고유 사용자 ID (Long 타입)
    private Long id;
}
