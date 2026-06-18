package com.umc10th.umc10th_hackathon_team_b_be.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import com.umc10th.umc10th_hackathon_team_b_be.global.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATED_USER_ID_ATTRIBUTE = "authenticatedUserId";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        if (uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.equals("/swagger-ui.html")
                || uri.equals("/error")) {
            return true;
        }

        if (!uri.startsWith("/api/v1")) {
            return true;
        }

        return ("POST".equals(request.getMethod()) && "/api/v1/auth-sessions".equals(uri))
                || ("POST".equals(request.getMethod()) && "/api/v1/users".equals(uri))
                || ("POST".equals(request.getMethod()) && "/api/v1/auth-tokens".equals(uri));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            writeUnauthorizedResponse(response);
            return;
        }

        String accessToken = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            Long userId = jwtTokenProvider.extractUserId(accessToken);
            request.setAttribute(AUTHENTICATED_USER_ID_ATTRIBUTE, userId);
        } catch (JwtException | IllegalArgumentException e) {
            writeUnauthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(ErrorCode.AUTH_401.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(ErrorCode.AUTH_401));
    }
}
