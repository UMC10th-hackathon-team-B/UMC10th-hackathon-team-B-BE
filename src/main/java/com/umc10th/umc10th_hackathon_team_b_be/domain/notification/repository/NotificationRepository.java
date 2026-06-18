package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    long countByUser_IdAndIsReadFalse(Long userId);
    Optional<Notification> findByIdAndUser_Id(Long notificationId, Long userId);
}
