package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.entity.OutingSession;

public interface OutingSessionRepository extends JpaRepository<OutingSession, Long> {
}
