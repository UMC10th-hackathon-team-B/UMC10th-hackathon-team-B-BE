package com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.controller.docs;

import org.springframework.http.ResponseEntity;

import com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.dto.AppLaunchRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AppLaunch", description = "앱 재실행 및 최초 진입 API")
public interface AppLaunchControllerDocs {

    @Operation(
            summary = "앱 재실행 및 최초 진입 상태 결정",
            description = "진행 중인 외출 세션 유무와 자동 종료 여부에 따라 홈/외출 화면 진입 데이터를 반환합니다.",
            operationId = "launch"
    )
    ResponseEntity<ApiResponse<OutingFlowResponse>> launch(
            @Parameter(hidden = true) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "앱 재실행 요청",
                    required = true
            )
            AppLaunchRequest request
    );
}
