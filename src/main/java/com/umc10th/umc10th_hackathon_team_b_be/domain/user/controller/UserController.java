package com.umc10th.umc10th_hackathon_team_b_be.domain.user.controller;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.controller.docs.UserControllerDocs;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.service.UserService;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @Override
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
        UserSignupResponse response = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
