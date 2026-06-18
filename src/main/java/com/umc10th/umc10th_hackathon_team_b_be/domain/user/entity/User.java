package com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_kakao_id", columnList = "kakao_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_kakao_id", columnNames = "kakao_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카카오 계정 식별자
    @Column(name = "kakao_id", nullable = false, length = 100)
    private String kakaoId;

    // 앱 접속 및 알림 이력
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "last_uv_notified_date")
    private LocalDate lastUvNotifiedDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 신규 유저 생성을 위한 빌더 추가
    @Builder
    public User(String kakaoId) {
        this.kakaoId = kakaoId;
    }
}
