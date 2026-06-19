package com.umc10th.umc10th_hackathon_team_b_be.domain.user.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(
            summary = "신규 사용자 생성 및 필수 약관 동의",
            description = "가입 토큰과 필수 약관 동의 목록을 검증하고 신규 사용자를 생성합니다.",
            operationId = "signup"
    )
    @SecurityRequirements()
    ResponseEntity<ApiResponse<UserSignupResponse>> signup(UserSignupRequest request);
}
