package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthLogoutRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthTokenReissueRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthTokenReissueResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "카카오 로그인 및 가입 여부 분기",
            description = "카카오 액세스 토큰을 전달받아 사용자 식별 후, 기존 회원이면 토큰 발급(HOME), 신규 유저면 가입 대기(TERMS) 상태로 분기합니다.",
            operationId = "createAuthSession"
    )
    ResponseEntity<ApiResponse<AuthSessionResponse>> createAuthSession(AuthSessionRequest request);

    @Operation(
            summary = "Access Token 재발급",
            description = "refresh token을 검증하고 access/refresh token을 재발급합니다.",
            operationId = "reissueAuthToken"
    )
    ResponseEntity<ApiResponse<AuthTokenReissueResponse>> reissueAuthToken(AuthTokenReissueRequest request);

    @Operation(
            summary = "로그아웃",
            description = "refresh token을 검증하고 무효화합니다. 진행 중인 외출 세션이 있으면 autoEndAt 경과 여부에 따라 AUTO 또는 LOGOUT으로 종료 처리합니다.",
            operationId = "logout"
    )
    ResponseEntity<ApiResponse<Void>> logout(AuthLogoutRequest request, @Parameter(hidden = true) Long userId);

}