package com.umc10th.umc10th_hackathon_team_b_be.domain.user.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.UserTermAgreement;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.enums.TermType;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserTermAgreementRepository;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String SIGNUP_TOKEN_PREFIX = "mock-signup-token-";
    private static final Set<TermType> REQUIRED_TERM_TYPES = EnumSet.of(
            TermType.SERVICE,
            TermType.PRIVACY,
            TermType.LOCATION
    );

    private final UserRepository userRepository;
    private final UserTermAgreementRepository userTermAgreementRepository;

    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {
        String kakaoId = extractKakaoIdFromSignupToken(request.getSignupToken());
        validateRequiredTerms(request.getAgreedTermTypes());
        validateUserNotExists(kakaoId);

        User user = userRepository.save(User.builder()
                .kakaoId(kakaoId)
                .build());

        List<UserTermAgreement> termAgreements = REQUIRED_TERM_TYPES.stream()
                .map(termType -> UserTermAgreement.builder()
                        .user(user)
                        .termType(termType)
                        .build())
                .toList();

        userTermAgreementRepository.saveAll(termAgreements);

        return UserSignupResponse.builder()
                .nextScreen("HOME")
                .userId(user.getId())
                .auth(buildMockAuthTokenInfo())
                .build();
    }

    private void validateUserNotExists(String kakaoId) {
        if (userRepository.existsByKakaoId(kakaoId)) {
            throw new BusinessException(ErrorCode.AUTH_400);
        }
    }

    private String extractKakaoIdFromSignupToken(String signupToken) {
        if (!StringUtils.hasText(signupToken)
                || !signupToken.startsWith(SIGNUP_TOKEN_PREFIX)
                || signupToken.length() <= SIGNUP_TOKEN_PREFIX.length()) {
            throw new BusinessException(ErrorCode.AUTH_400);
        }

        return signupToken.substring(SIGNUP_TOKEN_PREFIX.length());
    }

    private void validateRequiredTerms(List<TermType> agreedTermTypes) {
        if (agreedTermTypes == null || agreedTermTypes.isEmpty()) {
            throw new BusinessException(ErrorCode.TERMS_400);
        }

        Set<TermType> agreedTermTypeSet = EnumSet.copyOf(agreedTermTypes);
        if (!agreedTermTypeSet.containsAll(REQUIRED_TERM_TYPES)) {
            throw new BusinessException(ErrorCode.TERMS_400);
        }
    }

    private UserSignupResponse.AuthTokenInfo buildMockAuthTokenInfo() {
        return UserSignupResponse.AuthTokenInfo.builder()
                .tokenType("Bearer")
                .accessToken("mock-access-token")
                .accessTokenExpiresInSeconds(1800)
                .refreshToken("mock-refresh-token")
                .refreshTokenExpiresAt(
                        LocalDateTime.now().plusDays(30).withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                .build();
    }
}
