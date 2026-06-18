package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;

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

    private final NotificationRepository notificationRepository;

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

        notification.markAsRead(LocalDateTime.now());

        return getUnreadNotifications(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }
}