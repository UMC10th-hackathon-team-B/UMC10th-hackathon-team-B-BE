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
	TERMS_400(HttpStatus.BAD_REQUEST, "TERMS_400", "필수 약관에 모두 동의해야 합니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
