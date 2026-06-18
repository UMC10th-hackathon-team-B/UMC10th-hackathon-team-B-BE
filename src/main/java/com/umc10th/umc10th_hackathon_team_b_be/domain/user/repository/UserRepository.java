package com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 카카오 ID로 가입된 유저 찾기
    Optional<User> findByKakaoId(String kakaoId);
}
