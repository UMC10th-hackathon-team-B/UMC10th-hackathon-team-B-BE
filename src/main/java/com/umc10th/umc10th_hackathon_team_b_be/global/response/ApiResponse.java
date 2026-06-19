package com.umc10th.umc10th_hackathon_team_b_be.global.response;

import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "공통 API 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private static final String SUCCESS_CODE = "COMMON_200";
	private static final String SUCCESS_MESSAGE = "요청에 성공했습니다.";

	@Schema(description = "요청 성공 여부", example = "true")
	private final boolean success;

	@Schema(description = "응답 코드", example = "COMMON_200")
	private final String code;

	@Schema(description = "응답 메시지", example = "요청에 성공했습니다.")
	private final String message;

	@Schema(description = "응답 데이터. 데이터가 없는 경우 null")
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
