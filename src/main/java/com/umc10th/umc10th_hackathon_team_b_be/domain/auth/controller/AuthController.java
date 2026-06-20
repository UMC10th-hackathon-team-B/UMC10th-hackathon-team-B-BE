package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.controller;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.controller.docs.AuthControllerDocs;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthLogoutRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthTokenReissueRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthTokenReissueResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service.AuthService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service.SubmissionTestAuthService;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final SubmissionTestAuthService submissionTestAuthService;

    @Override
    @PostMapping("/auth-sessions")
    public ResponseEntity<ApiResponse<AuthSessionResponse>> createAuthSession(
            @Valid @RequestBody AuthSessionRequest request) {

        AuthSessionResponse response = authService.processKakaoLogin(request);

        // TODO: ApiResponse.onSuccess() 등 공통 래퍼 클래스 구현 방식에 맞게 리턴
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PostMapping("/auth-sessions/test")
    public ResponseEntity<ApiResponse<AuthSessionResponse>> createSubmissionTestAuthSession() {
        AuthSessionResponse response = submissionTestAuthService.issueSubmissionTestAuthSession();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PostMapping("/auth-tokens")
    public ResponseEntity<ApiResponse<AuthTokenReissueResponse>> reissueAuthToken(
            @Valid @RequestBody AuthTokenReissueRequest request) {
        AuthTokenReissueResponse response = authService.reissueAuthTokens(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @DeleteMapping("/auth-sessions/current")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody AuthLogoutRequest request,
            @CurrentUserId Long userId
    ) {
        authService.logout(request, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
