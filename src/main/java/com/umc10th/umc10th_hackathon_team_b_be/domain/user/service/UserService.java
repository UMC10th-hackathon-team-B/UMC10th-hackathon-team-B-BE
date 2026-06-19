package com.umc10th.umc10th_hackathon_team_b_be.domain.user.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.IssuedAuthTokens;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service.AuthTokenIssueService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service.SignupTokenService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.dto.UserSignupResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.UserTermAgreement;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.enums.TermType;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserTermAgreementRepository;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Set<TermType> REQUIRED_TERM_TYPES = EnumSet.of(
            TermType.SERVICE,
            TermType.PRIVACY,
            TermType.LOCATION
    );

    private final UserRepository userRepository;
    private final UserTermAgreementRepository userTermAgreementRepository;
    private final AuthTokenIssueService authTokenIssueService;
    private final SignupTokenService signupTokenService;
    private final Clock clock;

    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {
        String kakaoId = signupTokenService.extractKakaoId(request.getSignupToken());
        validateRequiredTerms(request.getAgreedTermTypes());
        validateUserNotExists(kakaoId);

        User user = userRepository.save(User.builder()
                .kakaoId(kakaoId)
                .build());

        List<UserTermAgreement> termAgreements = REQUIRED_TERM_TYPES.stream()
                .map(termType -> UserTermAgreement.builder()
                        .user(user)
                        .termType(termType)
                        .agreedAt(LocalDateTime.now(clock))
                        .build())
                .toList();

        userTermAgreementRepository.saveAll(termAgreements);

        IssuedAuthTokens issuedAuthTokens = authTokenIssueService.issueTokens(user);

        return UserSignupResponse.builder()
                .nextScreen("HOME")
                .userId(user.getId())
                .auth(UserSignupResponse.AuthTokenInfo.builder()
                        .tokenType(issuedAuthTokens.getTokenType())
                        .accessToken(issuedAuthTokens.getAccessToken())
                        .accessTokenExpiresInSeconds(issuedAuthTokens.getAccessTokenExpiresInSeconds())
                        .refreshToken(issuedAuthTokens.getRefreshToken())
                        .refreshTokenExpiresAt(issuedAuthTokens.getRefreshTokenExpiresAt())
                        .build())
                .build();
    }

    private void validateUserNotExists(String kakaoId) {
        if (userRepository.existsByKakaoId(kakaoId)) {
            throw new BusinessException(ErrorCode.AUTH_400);
        }
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
}
