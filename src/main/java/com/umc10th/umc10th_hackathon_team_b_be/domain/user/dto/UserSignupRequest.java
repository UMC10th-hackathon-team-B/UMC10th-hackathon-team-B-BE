package com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.enums.TermType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignupRequest {

    @Schema(description = "카카오 로그인 후 신규 사용자에게 발급된 가입 토큰", example = "signup-token-example")
    @NotBlank(message = "가입 토큰은 필수입니다.")
    private String signupToken;

    @Schema(
            description = "사용자가 동의한 필수 약관 타입 목록",
            example = "[\"SERVICE\", \"PRIVACY\", \"LOCATION\"]"
    )
    @NotEmpty(message = "동의한 약관 목록은 비어 있을 수 없습니다.")
    private List<TermType> agreedTermTypes;
}
