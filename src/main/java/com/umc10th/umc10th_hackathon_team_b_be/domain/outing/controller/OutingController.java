package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.controller.docs.OutingControllerDocs;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.LocationRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionCreateRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionEndRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.SunscreenApplicationRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.service.OutingService;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import com.umc10th.umc10th_hackathon_team_b_be.global.security.CurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/outing-sessions")
@RequiredArgsConstructor
public class OutingController implements OutingControllerDocs {

    private final OutingService outingService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<OutingFlowResponse>> createOutingSession(
            @CurrentUserId Long userId,
            @Valid @RequestBody OutingSessionCreateRequest request
    ) {
        OutingFlowResponse response = outingService.createSession(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<OutingFlowResponse>> getCurrentOutingSession(
            @CurrentUserId Long userId,
            @Valid @ModelAttribute LocationRequest request
    ) {
        OutingFlowResponse response = outingService.getCurrentSession(userId, request.latitude(), request.longitude());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PostMapping("/current/sunscreen-applications")
    public ResponseEntity<ApiResponse<OutingFlowResponse>> applySunscreen(
            @CurrentUserId Long userId,
            @Valid @RequestBody SunscreenApplicationRequest request
    ) {
        OutingFlowResponse response = outingService.applySunscreen(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PatchMapping("/{outingSessionId}")
    public ResponseEntity<ApiResponse<OutingFlowResponse>> completeOutingSession(
            @CurrentUserId Long userId,
            @PathVariable Long outingSessionId,
            @Valid @RequestBody OutingSessionEndRequest request
    ) {
        OutingFlowResponse response = outingService.completeSession(userId, outingSessionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
