package com.umc10th.umc10th_hackathon_team_b_be.global.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
		GlobalExceptionHandler.class,
		GlobalExceptionHandlerTest.TestController.class
})
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private com.umc10th.umc10th_hackathon_team_b_be.global.security.JwtTokenProvider jwtTokenProvider;

	@Test
	void businessExceptionReturnsErrorCodeResponse() throws Exception {
		mockMvc.perform(get("/test/business"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMON_404"))
				.andExpect(jsonPath("$.message").value("요청한 리소스를 찾을 수 없습니다."))
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void invalidRequestReturnsBadRequestResponse() throws Exception {
		mockMvc.perform(post("/test/validation")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMON_400"))
				.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void unknownExceptionReturnsInternalServerErrorResponse() throws Exception {
		mockMvc.perform(get("/test/runtime"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMON_500"))
				.andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."))
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@RestController
	@RequestMapping("/test")
	static class TestController {

		@GetMapping("/business")
		void business() {
			throw new BusinessException(ErrorCode.COMMON_404);
		}

		@PostMapping("/validation")
		void validation(@Valid @RequestBody TestRequest request) {
		}

		@GetMapping("/runtime")
		void runtime() {
			throw new RuntimeException("unexpected");
		}
	}

	record TestRequest(
			@NotBlank
			String name
	) {
	}
}
