package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.EggStatus;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionEndReason;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionStatus;
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
        name = "outing_sessions",
        indexes = {
                @Index(name = "idx_outing_sessions_user_id", columnList = "user_id"),
                @Index(name = "idx_outing_sessions_status", columnList = "status"),
                @Index(name = "idx_outing_sessions_user_status", columnList = "user_id, status"),
                @Index(name = "idx_outing_sessions_auto_end_at", columnList = "auto_end_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외출 세션 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 세션 상태 기본값
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20,
            columnDefinition = "varchar(20) default 'IN_PROGRESS'"
    )
    private OutingSessionStatus status = OutingSessionStatus.IN_PROGRESS;

    // 세션 종료 사유
    @Enumerated(EnumType.STRING)
    @Column(name = "end_reason", length = 20)
    private OutingSessionEndReason endReason;

    // 계란 상태 기본값
    @Enumerated(EnumType.STRING)
    @Column(
            name = "egg_status",
            nullable = false,
            length = 20,
            columnDefinition = "varchar(20) default 'SAFE'"
    )
    private EggStatus eggStatus = EggStatus.SAFE;

    @Column(name = "egg_score", nullable = false, precision = 6, scale = 2, columnDefinition = "decimal(6,2) default 0.00")
    private BigDecimal eggScore = BigDecimal.ZERO;

    // 자외선 지수 스냅샷
    @Column(name = "start_uv_index", nullable = false, precision = 4, scale = 2)
    private BigDecimal startUvIndex;

    @Column(name = "current_uv_index", nullable = false, precision = 4, scale = 2)
    private BigDecimal currentUvIndex;

    @Column(name = "last_sunscreen_applied_at")
    private LocalDateTime lastSunscreenAppliedAt;

    @Column(name = "last_calculated_at", nullable = false)
    private LocalDateTime lastCalculatedAt;

    // 외출 시간 범위
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "auto_end_at", nullable = false)
    private LocalDateTime autoEndAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
