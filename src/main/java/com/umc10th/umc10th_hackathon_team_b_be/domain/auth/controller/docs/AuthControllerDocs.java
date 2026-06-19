package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.controller.docs;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthLogoutRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthTokenReissueRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthTokenReissueResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.config.SwaggerErrorExamples;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "카카오 로그인, 토큰 재발급, 로그아웃 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "카카오 로그인 및 가입 여부 분기",
            description = "카카오 OAuth access token으로 사용자를 식별합니다. 기존 가입자는 nextScreen=HOME과 인증 토큰을 받고, 신규 사용자는 nextScreen=TERMS와 회원가입용 signupToken을 받습니다.",
            operationId = "createAuthSession"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기존 가입자는 인증 토큰 반환, 신규 사용자는 가입 토큰 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "카카오 토큰이 유효하지 않거나 로그인 요청값이 잘못된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400),
                                    @ExampleObject(name = "AUTH_400", value = SwaggerErrorExamples.AUTH_400)
                            }
                    )
            )
    })
    @SecurityRequirements()
    ResponseEntity<ApiResponse<AuthSessionResponse>> createAuthSession(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "카카오 로그인 세션 생성 요청",
                    required = true
            )
            AuthSessionRequest request
    );

    @Operation(
            summary = "Access Token 재발급",
            description = "refresh token을 검증한 뒤 새 access token과 refresh token을 재발급합니다. Authorization 헤더 없이 호출합니다.",
            operationId = "reissueAuthToken"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급된 access/refresh token 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "refresh token 요청값이 잘못된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "refresh token이 유효하지 않거나 만료된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            )
    })
    @SecurityRequirements()
    ResponseEntity<ApiResponse<AuthTokenReissueResponse>> reissueAuthToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "토큰 재발급 요청",
                    required = true
            )
            AuthTokenReissueRequest request
    );

    @Operation(
            summary = "로그아웃",
            description = "현재 refresh token을 무효화합니다. 진행 중인 외출 세션이 있으면 로그아웃 전에 종료하며, autoEndAt이 지난 경우 LOGOUT이 아니라 AUTO 사유로 종료됩니다.",
            operationId = "logout"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공. data는 null"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "refresh token 요청값이 잘못된 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "COMMON_400", value = SwaggerErrorExamples.COMMON_400)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Access Token 또는 refresh token이 유효하지 않은 경우",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "AUTH_401", value = SwaggerErrorExamples.AUTH_401)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그아웃 요청",
                    required = true
            )
            AuthLogoutRequest request,
            @Parameter(hidden = true) Long userId
    );

}
