package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.controller.docs;

import org.springframework.http.ResponseEntity;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.LocationRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionCreateRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionEndRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.SunscreenApplicationRequest;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@Tag(name = "Outing", description = "외출 세션 관련 API")
public interface OutingControllerDocs {

    @Operation(
            summary = "외출 세션 시작",
            description = "자외선 차단제 선택값과 현재 위치를 기준으로 외출 세션을 시작합니다.",
            operationId = "createOutingSession"
    )
    ResponseEntity<ApiResponse<OutingFlowResponse>> createOutingSession(
            @Parameter(hidden = true) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "외출 세션 시작 요청",
                    required = true
            )
            OutingSessionCreateRequest request
    );

    @Operation(
            summary = "외출 모드 화면 조회 및 새로고침",
            description = "진행 중인 외출 세션을 최신 날씨/자외선 정보로 재계산합니다.",
            operationId = "getCurrentOutingSession"
    )
    ResponseEntity<ApiResponse<OutingFlowResponse>> getCurrentOutingSession(
            @Parameter(hidden = true) Long userId,
            @ParameterObject LocationRequest request
    );

    @Operation(
            summary = "자외선 차단제 다시 바르기 기록",
            description = "현재 시각으로 차단제 기록을 남긴 뒤 외출 화면 데이터를 재계산합니다.",
            operationId = "applySunscreen"
    )
    ResponseEntity<ApiResponse<OutingFlowResponse>> applySunscreen(
            @Parameter(hidden = true) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "자외선 차단제 다시 바르기 요청",
                    required = true
            )
            SunscreenApplicationRequest request
    );

    @Operation(
            summary = "외출 세션 직접 종료",
            description = "진행 중인 외출 세션을 수동 종료합니다.",
            operationId = "completeOutingSession"
    )
    ResponseEntity<ApiResponse<OutingFlowResponse>> completeOutingSession(
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "종료할 외출 세션 ID", example = "10") Long outingSessionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "외출 세션 직접 종료 요청",
                    required = true
            )
            OutingSessionEndRequest request
    );
}
