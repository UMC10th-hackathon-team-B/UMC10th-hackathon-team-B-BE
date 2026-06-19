package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    long countByUser_IdAndIsReadFalse(Long userId);
    long countByUser_Id(Long userId);
    List<Notification> findByUser_IdOrderByCreatedAtAscIdAsc(Long userId, Pageable pageable);
    Optional<Notification> findByIdAndUser_Id(Long notificationId, Long userId);
}
