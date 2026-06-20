package com.umc10th.umc10th_hackathon_team_b_be.domain.auth.service;

import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.AuthSessionResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.auth.dto.IssuedAuthTokens;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.entity.Notification;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.repository.NotificationRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmissionTestAuthService {

    private static final String SUBMISSION_TEST_KAKAO_ID = "submission-test-user";
    private static final Set<TermType> REQUIRED_TERM_TYPES = EnumSet.of(
            TermType.SERVICE,
            TermType.PRIVACY,
            TermType.LOCATION
    );

    private final UserRepository userRepository;
    private final UserTermAgreementRepository userTermAgreementRepository;
    private final NotificationRepository notificationRepository;
    private final AuthTokenIssueService authTokenIssueService;
    private final Clock clock;

    @Value("${app.submission-test.enabled:false}")
    private boolean enabled;

    @Transactional
    public AuthSessionResponse issueSubmissionTestAuthSession() {
        if (!enabled) {
            throw new BusinessException(ErrorCode.COMMON_404);
        }

        User user = userRepository.findByKakaoId(SUBMISSION_TEST_KAKAO_ID)
                .orElseGet(this::createSubmissionTestUser);

        ensureRequiredTermAgreements(user);
        ensureDummyNotifications(user);

        IssuedAuthTokens issuedAuthTokens = authTokenIssueService.issueTokens(user);

        return AuthSessionResponse.builder()
                .nextScreen("HOME")
                .userId(user.getId())
                .auth(AuthSessionResponse.AuthTokenInfo.builder()
                        .tokenType(issuedAuthTokens.getTokenType())
                        .accessToken(issuedAuthTokens.getAccessToken())
                        .accessTokenExpiresInSeconds(issuedAuthTokens.getAccessTokenExpiresInSeconds())
                        .refreshToken(issuedAuthTokens.getRefreshToken())
                        .refreshTokenExpiresAt(issuedAuthTokens.getRefreshTokenExpiresAt())
                        .build())
                .build();
    }

    private User createSubmissionTestUser() {
        return userRepository.save(User.builder()
                .kakaoId(SUBMISSION_TEST_KAKAO_ID)
                .build());
    }

    private void ensureRequiredTermAgreements(User user) {
        LocalDateTime agreedAt = LocalDateTime.now(clock);

        List<UserTermAgreement> missingTermAgreements = REQUIRED_TERM_TYPES.stream()
                .filter(termType -> !userTermAgreementRepository.existsByUser_IdAndTermType(user.getId(), termType))
                .map(termType -> UserTermAgreement.builder()
                        .user(user)
                        .termType(termType)
                        .agreedAt(agreedAt)
                        .build())
                .toList();

        if (!missingTermAgreements.isEmpty()) {
            userTermAgreementRepository.saveAll(missingTermAgreements);
        }
    }

    private void ensureDummyNotifications(User user) {
        if (notificationRepository.countByUser_Id(user.getId()) > 0) {
            return;
        }

        notificationRepository.saveAll(List.of(
                Notification.createDailyUv(
                        user,
                        "오늘 자외선 지수가 높아요",
                        "외출 시 모자나 선글라스를 챙기고, 차단제를 꼼꼼히 바르세요."
                ),
                Notification.createEggDanger(user, null)
        ));
    }
}
