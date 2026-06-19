package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.EggStatus;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionEndReason;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "앱 진입 및 외출 세션 흐름 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OutingFlowResponse(

        @Schema(description = "다음 화면", example = "OUTING")
        String nextScreen,

        @Schema(description = "홈 화면 데이터")
        HomeResponse home,

        @Schema(description = "외출 화면 데이터")
        OutingResponse outing,

        @Schema(description = "종료된 외출 세션")
        EndedSessionResponse endedSession,

        @Schema(description = "자동 종료 안내")
        AutoEndNoticeResponse autoEndNotice
) {

    public static OutingFlowResponse home(HomeResponse home) {
        return new OutingFlowResponse("HOME", home, null, null, null);
    }

    public static OutingFlowResponse outing(OutingResponse outing) {
        return new OutingFlowResponse("OUTING", null, outing, null, null);
    }

    public static OutingFlowResponse ended(EndedSessionResponse endedSession) {
        return new OutingFlowResponse("HOME", null, null, endedSession, null);
    }

    public static OutingFlowResponse autoEnded(
            EndedSessionResponse endedSession,
            AutoEndNoticeResponse autoEndNotice
    ) {
        return new OutingFlowResponse("HOME", null, null, endedSession, autoEndNotice);
    }

    @Schema(description = "홈 화면 데이터")
    public record HomeResponse(
            WeatherResponse weather,
            EggResponse egg,
            OutingStartResponse outingStart,
            NotificationSummaryResponse notification
    ) {
    }

    @Schema(description = "외출 화면 데이터")
    public record OutingResponse(
            OutingSessionSummaryResponse outingSession,
            WeatherResponse weather,
            EggResponse egg,
            SunscreenResponse sunscreen,
            NotificationSummaryResponse notification
    ) {
    }

    @Schema(description = "외출 세션 요약")
    public record OutingSessionSummaryResponse(
            Long outingSessionId,
            LocalDateTime startedAt,
            LocalDateTime autoEndAt,
            long elapsedMinutes,
            String elapsedTimeText
    ) {
    }

    @Schema(description = "외출 화면에서 사용하는 날씨 요약")
    public record WeatherResponse(
            String locationName,
            String weatherType,
            String weatherLabel,
            BigDecimal temperatureCelsius,
            BigDecimal uvIndex,
            String uvLevel,
            String uvLevelLabel
    ) {
    }

    @Schema(description = "계란 상태")
    public record EggResponse(
            EggStatus eggStatus,
            String eggStatusLabel,
            String message
    ) {
    }

    @Schema(description = "차단제 기록")
    public record SunscreenResponse(
            LocalDateTime lastSunscreenAppliedAt,
            Long lastSunscreenAppliedElapsedMinutes,
            String lastSunscreenAppliedText
    ) {
    }

    @Schema(description = "외출 시작 가능 여부")
    public record OutingStartResponse(
            boolean canStart,
            String unavailableMessage
    ) {
    }

    @Schema(description = "알림 요약")
    public record NotificationSummaryResponse(
            long unreadCount
    ) {
    }

    @Schema(description = "종료된 세션 정보")
    public record EndedSessionResponse(
            Long outingSessionId,
            OutingSessionStatus status,
            OutingSessionEndReason endReason,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            long elapsedMinutes,
            String elapsedTimeText
    ) {
    }

    @Schema(description = "자동 종료 안내")
    public record AutoEndNoticeResponse(
            boolean showPopup,
            String title,
            String message
    ) {
    }
}
