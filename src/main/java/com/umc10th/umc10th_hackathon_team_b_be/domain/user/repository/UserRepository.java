package com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository;

import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(String kakaoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("""
            update User u
            set u.lastUvNotifiedDate = :today
            where u.id = :userId
              and (u.lastUvNotifiedDate is null or u.lastUvNotifiedDate <> :today)
            """)
    int markUvNotifiedIfNotToday(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );

    boolean existsByKakaoId(String kakaoId);
}
