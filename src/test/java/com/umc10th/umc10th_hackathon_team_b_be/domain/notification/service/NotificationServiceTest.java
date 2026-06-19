package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.entity.Notification;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.enums.NotificationType;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.repository.NotificationRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.entity.OutingSession;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Long USER_ID = 1L;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @ParameterizedTest
    @MethodSource("dailyUvNotifications")
    void createDailyUvIfNeededCreatesNotificationByUvLevel(
            double uvIndex,
            String expectedTitle,
            String expectedContent
    ) {
        Clock clock = fixedClock("2026-06-19T03:00:00Z");
        NotificationService service = new NotificationService(notificationRepository, userRepository, clock);
        User user = User.builder()
                .kakaoId("kakao-id")
                .build();

        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));

        service.createDailyUvIfNeeded(USER_ID, uvIndex);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();

        assertThat(notification.getUser()).isSameAs(user);
        assertThat(notification.getOutingSession()).isNull();
        assertThat(notification.getType()).isEqualTo(NotificationType.DAILY_UV);
        assertThat(notification.getTitle()).isEqualTo(expectedTitle);
        assertThat(notification.getContent()).isEqualTo(expectedContent);
        assertThat(notification.getIsRead()).isFalse();
        assertThat(user.getLastUvNotifiedDate()).isEqualTo(LocalDate.of(2026, 6, 19));
    }

    @Test
    void createDailyUvIfNeededDoesNotCreateOutsideDailyUvTime() {
        Clock clock = fixedClock("2026-06-18T20:59:00Z");
        NotificationService service = new NotificationService(notificationRepository, userRepository, clock);

        service.createDailyUvIfNeeded(USER_ID, 7.2);

        verifyNoInteractions(userRepository, notificationRepository);
    }

    @Test
    void createDailyUvIfNeededDoesNotCreateDuplicateInSameDate() {
        Clock clock = fixedClock("2026-06-19T03:00:00Z");
        NotificationService service = new NotificationService(notificationRepository, userRepository, clock);
        User user = User.builder()
                .kakaoId("kakao-id")
                .build();
        user.markUvNotified(LocalDate.of(2026, 6, 19));

        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));

        service.createDailyUvIfNeeded(USER_ID, 7.2);

        verify(notificationRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(notificationRepository, never()).countByUser_Id(USER_ID);
    }

    @Test
    void createDailyUvIfNeededDeletesOldestNotificationWhenRetentionLimitExceeded() {
        Clock clock = fixedClock("2026-06-19T03:00:00Z");
        NotificationService service = new NotificationService(notificationRepository, userRepository, clock);
        User user = User.builder()
                .kakaoId("kakao-id")
                .build();
        Notification oldNotification = Notification.createDailyUv(
                user,
                "old title",
                "old content"
        );

        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
        when(notificationRepository.countByUser_Id(USER_ID)).thenReturn(31L);
        when(notificationRepository.findByUser_IdOrderByCreatedAtAscIdAsc(eq(USER_ID), any(Pageable.class)))
                .thenReturn(List.of(oldNotification));

        service.createDailyUvIfNeeded(USER_ID, 7.2);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(notificationRepository).findByUser_IdOrderByCreatedAtAscIdAsc(eq(USER_ID), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(1);
        verify(notificationRepository).deleteAllInBatch(List.of(oldNotification));
    }

    @Test
    void createEggDangerDeletesOldestNotificationsWhenRetentionLimitExceeded() {
        Clock clock = fixedClock("2026-06-19T03:00:00Z");
        NotificationService service = new NotificationService(notificationRepository, userRepository, clock);
        User user = mock(User.class);
        OutingSession outingSession = mock(OutingSession.class);
        Notification firstOldNotification = Notification.createDailyUv(
                user,
                "first old title",
                "first old content"
        );
        Notification secondOldNotification = Notification.createDailyUv(
                user,
                "second old title",
                "second old content"
        );

        when(user.getId()).thenReturn(USER_ID);
        when(outingSession.getUser()).thenReturn(user);
        when(notificationRepository.countByUser_Id(USER_ID)).thenReturn(32L);
        when(notificationRepository.findByUser_IdOrderByCreatedAtAscIdAsc(eq(USER_ID), any(Pageable.class)))
                .thenReturn(List.of(firstOldNotification, secondOldNotification));

        service.createEggDanger(outingSession);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationRepository).findByUser_IdOrderByCreatedAtAscIdAsc(eq(USER_ID), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(2);
        verify(notificationRepository).deleteAllInBatch(List.of(firstOldNotification, secondOldNotification));
    }

    @Test
    void createDailyUvIfNeededThrowsUser404WhenUserNotFound() {
        Clock clock = fixedClock("2026-06-19T03:00:00Z");
        NotificationService service = new NotificationService(notificationRepository, userRepository, clock);

        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createDailyUvIfNeeded(USER_ID, 7.2))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_404);

        verify(notificationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private static Stream<Arguments> dailyUvNotifications() {
        return Stream.of(
                Arguments.of(
                        2.0,
                        "오늘 자외선은 안심 수준이에요",
                        "현재 자외선 지수는 안심 수준이에요."
                ),
                Arguments.of(
                        5.0,
                        "오늘 자외선은 보통이에요",
                        "장시간 외출 시에는 자외선 차단제를 발라주세요."
                ),
                Arguments.of(
                        7.0,
                        "오늘 자외선 지수가 높아요",
                        "외출 시 모자나 선글라스를 챙기고, 차단제를 꼼꼼히 바르세요."
                ),
                Arguments.of(
                        10.0,
                        "오늘 자외선이 매우 강해요",
                        "자외선 지수가 매우 높음 단계입니다! 되도록 실내에 머무르세요."
                ),
                Arguments.of(
                        11.0,
                        "오늘 자외선이 위험해요",
                        "위험 수준의 자외선 지수입니다! 외출을 최대한 자제하시고 피부 보호에 신경 쓰세요."
                )
        );
    }

    private static Clock fixedClock(String instant) {
        return Clock.fixed(Instant.parse(instant), ZONE_ID);
    }
}
