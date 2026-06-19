package com.umc10th.umc10th_hackathon_team_b_be.domain.user.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "신규 사용자 가입 및 약관 동의 API")
public interface UserControllerDocs {

    @Operation(
            summary = "신규 사용자 생성 및 필수 약관 동의",
            description = "카카오 로그인 후 발급된 signupToken과 필수 약관 동의 목록을 검증한 뒤 사용자를 생성합니다. 성공 시 nextScreen=HOME과 인증 토큰을 반환합니다.",
            operationId = "signup"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공 및 인증 토큰 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "가입 토큰이 유효하지 않거나 필수 약관 동의가 누락된 경우")
    })
    @SecurityRequirements()
    ResponseEntity<ApiResponse<UserSignupResponse>> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "신규 사용자 생성 요청",
                    required = true
            )
            UserSignupRequest request
    );
}
