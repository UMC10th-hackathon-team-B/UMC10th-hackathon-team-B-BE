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
	COMMON_500(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
