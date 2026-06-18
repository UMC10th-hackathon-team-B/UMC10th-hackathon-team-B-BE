package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.enums.NotificationType;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.entity.OutingSession;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user_id", columnList = "user_id"),
                @Index(name = "idx_notifications_user_read_created", columnList = "user_id, is_read, created_at DESC"),
                @Index(name = "idx_notifications_user_created", columnList = "user_id, created_at DESC"),
                @Index(name = "idx_notifications_session_id", columnList = "outing_session_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 수신 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 관련 외출 세션
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outing_session_id")
    private OutingSession outingSession;

    // 알림 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 읽음 상태 기본값
    @Column(name = "is_read", nullable = false, columnDefinition = "boolean default false")
    private Boolean isRead = false;

    public void markAsRead(LocalDateTime readAt) {
        this.isRead = true;
        this.readAt = readAt;
    }

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
