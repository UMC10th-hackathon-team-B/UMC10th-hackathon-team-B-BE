package com.umc10th.umc10th_hackathon_team_b_be.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenApiConfig {

	private final String serverUrl;

	public OpenApiConfig(@Value("${app.openapi.server-url:}") String serverUrl) {
		this.serverUrl = serverUrl;
	}

	// Swagger API document default settings
	@Bean
	public OpenAPI openAPI() {
		OpenAPI openAPI = new OpenAPI()
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

				.info(new Info()
						.title("UMC 10th Hackathon Team B Backend API")
						.version("v1"));

		if (StringUtils.hasText(serverUrl)) {
			openAPI.servers(List.of(new Server().url(serverUrl)));
		}

		return openAPI;
	}
}
