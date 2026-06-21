package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.EggStatus;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionEndReason;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "앱 진입, 외출 세션, 자동 종료 흐름 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OutingFlowResponse(

        @Schema(description = "다음 화면. 홈 모드는 HOME, 외출 모드는 OUTING", example = "OUTING", allowableValues = {"HOME", "OUTING"})
        String nextScreen,

        @Schema(description = "홈 화면 데이터. nextScreen=HOME이고 일반 홈 진입인 경우 포함", nullable = true)
        HomeResponse home,

        @Schema(description = "외출 화면 데이터. nextScreen=OUTING인 경우 포함", nullable = true)
        OutingResponse outing,

        @Schema(description = "종료된 외출 세션 정보. 수동 종료 또는 자동 종료 응답인 경우 포함", nullable = true)
        EndedSessionResponse endedSession,

        @Schema(description = "자동 종료 안내 팝업 정보. autoEndAt 경과 후 접근한 경우 포함", nullable = true)
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
            @Schema(description = "홈 화면에 표시할 위치, 날씨, 기온, 자외선 정보")
            WeatherResponse weather,

            @Schema(description = "홈 모드 계란 상태와 화면 문구")
            EggResponse egg,

            @Schema(description = "현재 시간 기준 외출 시작 가능 여부")
            OutingStartResponse outingStart,

            @Schema(description = "읽지 않은 알림 개수 요약")
            NotificationSummaryResponse notification
    ) {
    }

    @Schema(description = "외출 화면 데이터")
    public record OutingResponse(
            @Schema(description = "진행 중인 외출 세션 시간 정보")
            OutingSessionSummaryResponse outingSession,

            @Schema(description = "외출 화면에 표시할 최신 위치, 날씨, 기온, 자외선 정보")
            WeatherResponse weather,

            @Schema(description = "자외선 노출과 차단제 기록을 반영한 계란 상태")
            EggResponse egg,

            @Schema(description = "마지막 자외선 차단제 기록 정보")
            SunscreenResponse sunscreen,

            @Schema(description = "읽지 않은 알림 개수 요약")
            NotificationSummaryResponse notification
    ) {
    }

    @Schema(description = "외출 세션 요약")
    public record OutingSessionSummaryResponse(
            @Schema(description = "외출 세션 ID", example = "10")
            Long outingSessionId,

            @Schema(description = "외출 시작 일시. Asia/Seoul 기준 ISO-8601 local datetime", example = "2026-06-18T09:10:00")
            LocalDateTime startedAt,

            @Schema(description = "자동 종료 예정 일시. 일반적으로 당일 20:00", example = "2026-06-18T20:00:00")
            LocalDateTime autoEndAt,

            @Schema(description = "외출 시작 후 경과 시간(분)", example = "75")
            long elapsedMinutes,

            @Schema(description = "화면 표시용 경과 시간", example = "1시간 15분")
            String elapsedTimeText
    ) {
    }

    @Schema(description = "날씨 및 자외선 요약")
    public record WeatherResponse(
            @Schema(description = "법정동 기준 위치명", example = "송파구 문정동")
            String locationName,

            @Schema(description = "날씨 타입", example = "CLEAR")
            String weatherType,

            @Schema(description = "날씨 표시명", example = "맑음")
            String weatherLabel,

            @Schema(description = "섭씨 온도", example = "24.6")
            BigDecimal temperatureCelsius,

            @Schema(description = "자외선 지수", example = "7.2")
            BigDecimal uvIndex,

            @Schema(description = "자외선 단계", example = "HIGH")
            String uvLevel,

            @Schema(description = "자외선 단계 표시명", example = "높음")
            String uvLevelLabel
    ) {
    }

    @Schema(description = "계란 상태")
    public record EggResponse(
            @Schema(description = "계란 상태", example = "LIGHT_TOASTED")
            EggStatus eggStatus,

            @Schema(description = "계란 상태 표시명", example = "살짝 노릇한 계란")
            String eggStatusLabel,

            @Schema(description = "화면 표시용 계란 상태 문구", example = "차단제 효과가 조금씩 줄어들고 있어요.")
            String message
    ) {
    }

    @Schema(description = "자외선 차단제 기록")
    public record SunscreenResponse(
            @Schema(description = "마지막 자외선 차단제 기록 일시", example = "2026-06-18T09:55:00")
            LocalDateTime lastSunscreenAppliedAt,

            @Schema(description = "마지막 차단제 기록 후 경과 시간(분)", example = "30")
            Long lastSunscreenAppliedElapsedMinutes,

            @Schema(description = "화면 표시용 마지막 차단제 기록 문구", example = "30분 전 마지막 기록")
            String lastSunscreenAppliedText
    ) {
    }

    @Schema(description = "외출 시작 가능 여부")
    public record OutingStartResponse(
            @Schema(description = "현재 시간 기준 외출 시작 가능 여부", example = "true")
            boolean canStart,

            @Schema(description = "외출 시작이 불가능한 경우 표시할 문구", example = "저녁 8시 이후에는 외출 모드를 시작할 수 없어요.", nullable = true)
            String unavailableMessage
    ) {
    }

    @Schema(description = "알림 요약")
    public record NotificationSummaryResponse(
            @Schema(description = "읽지 않은 알림 개수", example = "2")
            long unreadCount
    ) {
    }

    @Schema(description = "종료된 세션 정보")
    public record EndedSessionResponse(
            @Schema(description = "종료된 외출 세션 ID", example = "10")
            Long outingSessionId,

            @Schema(description = "외출 세션 상태", example = "COMPLETED")
            OutingSessionStatus status,

            @Schema(description = "종료 사유. MANUAL은 직접 종료, AUTO는 자동 종료, LOGOUT은 로그아웃 종료", example = "AUTO")
            OutingSessionEndReason endReason,

            @Schema(description = "외출 시작 일시", example = "2026-06-18T09:10:00")
            LocalDateTime startedAt,

            @Schema(description = "외출 종료 일시. 자동 종료 시 autoEndAt과 동일", example = "2026-06-18T20:00:00")
            LocalDateTime endedAt,

            @Schema(description = "총 외출 시간(분)", example = "650")
            long elapsedMinutes,

            @Schema(description = "화면 표시용 총 외출 시간", example = "10시간 50분")
            String elapsedTimeText
    ) {
    }

    @Schema(description = "자동 종료 안내")
    public record AutoEndNoticeResponse(
            @Schema(description = "홈 이동 전 자동 종료 안내 팝업 표시 여부", example = "true")
            boolean showPopup,

            @Schema(description = "자동 종료 안내 제목", example = "자외선 관리 시간이 종료됐어요")
            String title,

            @Schema(description = "자동 종료 안내 메시지", example = "저녁 8시 이후에는 외출 모드가 자동으로 종료돼요. 외출 기록을 저장하고 홈 모드로 이동할게요.")
            String message
    ) {
    }
}
