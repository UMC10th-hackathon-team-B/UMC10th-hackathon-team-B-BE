package com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.controller.docs.AppLaunchControllerDocs;
import com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.dto.AppLaunchRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.service.AppLaunchService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/app-launches")
@RequiredArgsConstructor
public class AppLaunchController implements AppLaunchControllerDocs {

    private final AppLaunchService appLaunchService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<OutingFlowResponse>> launch(
            @CurrentUserId Long userId,
            @Valid @RequestBody AppLaunchRequest request
    ) {
        OutingFlowResponse response = appLaunchService.launch(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
