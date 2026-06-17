package com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.UserTermAgreement;

public interface UserTermAgreementRepository extends JpaRepository<UserTermAgreement, Long> {
}
