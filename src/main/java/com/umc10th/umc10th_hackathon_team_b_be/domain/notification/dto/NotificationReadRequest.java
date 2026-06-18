package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;

@Schema(description = "알림 읽음 처리 요청")
public record NotificationReadRequest(

        @AssertTrue(message = "읽음 처리 요청값은 true여야 합니다.")
        @Schema(description = "읽음 여부", example = "true")
        boolean isRead
) {
}