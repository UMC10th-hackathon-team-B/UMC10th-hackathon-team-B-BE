package com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.enums.TermType;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "user_term_agreements",
        indexes = {
                @Index(name = "idx_user_term_agreements_user_id", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_term_agreements_user_type",
                        columnNames = {"user_id", "term_type"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTermAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 약관 동의 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 필수 약관 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false, length = 20)
    private TermType termType;

    @Column(name = "agreed_at", nullable = false)
    private LocalDateTime agreedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 약관 동의 저장을 위한 빌더 추가
    @Builder
    public UserTermAgreement(User user, TermType termType, LocalDateTime agreedAt) {
        this.user = user;
        this.termType = termType;
        this.agreedAt = agreedAt;
    }
}
