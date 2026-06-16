package com.umc10th.umc10th_hackathon_team_b_be.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	// Swagger API 문서 기본 정보 설정
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("UMC 10th Hackathon Team B Backend API")
						.version("v1"));
	}
}
