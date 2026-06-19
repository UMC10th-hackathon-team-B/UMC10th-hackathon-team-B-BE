package com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.controller.docs;

import org.springframework.http.ResponseEntity;

import com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.dto.AppLaunchRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AppLaunch", description = "앱 재실행 및 최초 진입 화면 분기 API")
public interface AppLaunchControllerDocs {

    @Operation(
            summary = "앱 재실행 및 최초 진입 상태 결정",
            description = "현재 위치와 진행 중인 외출 세션 상태를 기준으로 nextScreen=HOME 또는 OUTING을 반환합니다. 외출 세션이 autoEndAt을 지난 경우 세션을 자동 종료하고 HOME 이동 데이터와 자동 종료 안내를 반환합니다.",
            operationId = "launch"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "홈/외출 화면 진입 데이터 또는 자동 종료 결과 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Access Token이 없거나 유효하지 않은 경우"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "날씨 또는 위치 외부 API 조회에 실패한 경우")
    })
    ResponseEntity<ApiResponse<OutingFlowResponse>> launch(
            @Parameter(hidden = true) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "앱 재실행 요청. 현재 위치 기반으로 날씨와 자외선 정보를 함께 조회합니다.",
                    required = true
            )
            AppLaunchRequest request
    );
}
