package com.umc10th.umc10th_hackathon_team_b_be.global.response;

import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private static final String SUCCESS_CODE = "COMMON_200";
	private static final String SUCCESS_MESSAGE = "요청에 성공했습니다.";

	private final boolean success;
	private final String code;
	private final String message;
	private final T data;

	// 성공 응답 공통 코드/메시지 적용
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, data);
	}

	public static ApiResponse<Void> success() {
		return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, null);
	}

	// ErrorCode 기반 실패 응답 생성
	public static ApiResponse<Void> failure(ErrorCode errorCode) {
		return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
	}

	public static ApiResponse<Void> failure(String code, String message) {
		return new ApiResponse<>(false, code, message, null);
	}
}
