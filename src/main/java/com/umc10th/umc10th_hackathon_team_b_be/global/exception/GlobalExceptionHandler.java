package com.umc10th.umc10th_hackathon_team_b_be.global.exception;

import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 비즈니스 예외를 공통 응답으로 변환
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();

		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.failure(errorCode));
	}

	// Request DTO 검증 실패를 공통 400 응답으로 변환
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException exception
	) {
		ErrorCode errorCode = ErrorCode.COMMON_400;

		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.failure(errorCode));
	}

	// 존재하지 않는 경로를 공통 404 응답으로 변환
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException exception) {
		ErrorCode errorCode = ErrorCode.COMMON_404;

		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.failure(errorCode));
	}

	// 예상하지 못한 예외 로깅 및 공통 500 응답 변환
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
		log.error("Unexpected exception occurred", exception);
		ErrorCode errorCode = ErrorCode.COMMON_500;

		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.failure(errorCode));
	}
}
