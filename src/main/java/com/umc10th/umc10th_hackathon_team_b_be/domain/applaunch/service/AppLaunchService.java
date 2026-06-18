package com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.service;

import org.springframework.stereotype.Service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.applaunch.dto.AppLaunchRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.service.OutingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppLaunchService {

    private final OutingService outingService;

    public OutingFlowResponse launch(Long userId, AppLaunchRequest request) {
        return outingService.handleAppLaunch(userId, request.latitude(), request.longitude());
    }
}
