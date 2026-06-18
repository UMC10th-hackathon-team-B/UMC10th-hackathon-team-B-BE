package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationItemResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.dto.NotificationListResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.entity.Notification;
import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final LocalTime DAILY_UV_START_TIME = LocalTime.of(6, 0);
    private static final LocalTime DAILY_UV_END_TIME = LocalTime.of(18, 0);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public NotificationListResponse getUnreadNotifications(Long userId) {
        List<NotificationItemResponse> notifications = notificationRepository
                .findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationItemResponse::from)
                .toList();

        return NotificationListResponse.of(notifications);
    }

    @Transactional
    public NotificationListResponse readNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_404));

        notification.markAsRead(LocalDateTime.now(clock));

        return getUnreadNotifications(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    @Transactional
    public void createDailyUvIfNeeded(Long userId, double uvIndex) {
        LocalDate today = LocalDate.now(clock);
        LocalTime now = LocalTime.now(clock);

        if (now.isBefore(DAILY_UV_START_TIME) || !now.isBefore(DAILY_UV_END_TIME)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_404));

        if (today.equals(user.getLastUvNotifiedDate())) {
            return;
        }

        notificationRepository.save(Notification.createDailyUv(
                user,
                resolveDailyUvTitle(uvIndex),
                resolveDailyUvContent(uvIndex)
        ));
        user.markUvNotified(today);
    }

    private String resolveDailyUvTitle(double uvIndex) {
        if (uvIndex <= 2) {
            return "오늘 자외선은 안심 수준이에요";
        }

        if (uvIndex <= 5) {
            return "오늘 자외선은 보통이에요";
        }

        if (uvIndex <= 7) {
            return "오늘 자외선 지수가 높아요";
        }

        if (uvIndex <= 10) {
            return "오늘 자외선이 매우 강해요";
        }

        return "오늘 자외선이 위험해요";
    }

    private String resolveDailyUvContent(double uvIndex) {
        if (uvIndex <= 2) {
            return "현재 자외선 지수는 안심 수준이에요.";
        }

        if (uvIndex <= 5) {
            return "현재 자외선 지수는 보통이에요. 장시간 외출 시에는 자외선 차단제를 발라주세요.";
        }

        if (uvIndex <= 7) {
            return "현재 자외선 지수가 높아요! 외출 시 모자나 선글라스를 챙기고, 차단제를 꼼꼼히 바르세요.";
        }

        if (uvIndex <= 10) {
            return "자외선 지수가 매우 높음 단계입니다! 되도록 실내에 머무르세요.";
        }

        return "위험 수준의 자외선 지수입니다! 외출을 최대한 자제하시고 피부 보호에 신경 쓰세요.";
    }
}
