package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.entity.OutingSession;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionStatus;

import jakarta.persistence.LockModeType;

public interface OutingSessionRepository extends JpaRepository<OutingSession, Long> {

    Optional<OutingSession> findFirstByUser_IdAndStatusOrderByStartedAtDesc(
            Long userId,
            OutingSessionStatus status
    );

    Optional<OutingSession> findByIdAndUser_IdAndStatus(
            Long id,
            Long userId,
            OutingSessionStatus status
    );

    Optional<OutingSession> findFirstByUser_IdOrderByStartedAtDesc(Long userId);

    long countByUser_IdAndStatus(Long userId, OutingSessionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select os
            from OutingSession os
            join fetch os.user
            where os.user.id = :userId
              and os.status = :status
            order by os.startedAt desc
            """)
    java.util.List<OutingSession> findCurrentSessionsByUserIdForUpdate(
            @Param("userId") Long userId,
            @Param("status") OutingSessionStatus status
    );
}
