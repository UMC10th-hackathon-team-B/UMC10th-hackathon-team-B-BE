package com.umc10th.umc10th_hackathon_team_b_be.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 도메인별 코드 추가 전 사용하는 공통 예외 코드
	COMMON_400(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
	COMMON_404(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 리소스를 찾을 수 없습니다."),
	COMMON_500(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다."),

	// --- Auth & User 도메인 예외 코드 ---
	AUTH_401(HttpStatus.UNAUTHORIZED, "AUTH_401", "인증이 필요합니다."),
	AUTH_400(HttpStatus.BAD_REQUEST, "AUTH_400", "유효하지 않은 로그인 요청입니다."),
	USER_404(HttpStatus.NOT_FOUND, "USER_404", "사용자를 찾을 수 없습니다."),
	TERMS_400(HttpStatus.BAD_REQUEST, "TERMS_400", "필수 약관에 모두 동의해야 합니다."),

    // --- Outing 도메인 예외 코드 ---
    OUTING_400(HttpStatus.BAD_REQUEST, "OUTING_400", "외출 세션을 시작할 수 없습니다."),
    OUTING_404(HttpStatus.NOT_FOUND, "OUTING_404", "진행 중인 외출 세션이 없습니다."),
    OUTING_409(HttpStatus.CONFLICT, "OUTING_409", "이미 진행 중인 외출 세션이 있습니다."),

    // --- Notification 도메인 예외 코드 ---
    NOTIFICATION_404(HttpStatus.NOT_FOUND, "NOTIFICATION_404", "알림을 찾을 수 없습니다."),

    // --- Weather 도메인 예외 코드 ---
    WEATHER_502(HttpStatus.BAD_GATEWAY, "WEATHER_502", "날씨 정보를 불러오지 못했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
