package com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.repository.NotificationRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.repository.UserRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NotificationDailyUvConcurrencyIntegrationTest {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private Clock clock;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 19, 12, 0);
        when(clock.getZone()).thenReturn(SEOUL_ZONE_ID);
        when(clock.instant()).thenReturn(now.atZone(SEOUL_ZONE_ID).toInstant());
    }

    @Test
    void createDailyUvIfNeededCreatesOnlyOnceForConcurrentRequests() throws Exception {
        User user = userRepository.save(User.builder()
                .kakaoId("notification-concurrency-" + UUID.randomUUID())
                .build());
        int requestCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch ready = new CountDownLatch(requestCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {
            futures.add(executorService.submit(() -> {
                ready.countDown();
                assertThat(start.await(3, TimeUnit.SECONDS)).isTrue();
                notificationService.createDailyUvIfNeeded(user.getId(), 7.2);
                return null;
            }));
        }

        assertThat(ready.await(3, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        for (Future<Void> future : futures) {
            future.get(5, TimeUnit.SECONDS);
        }
        executorService.shutdownNow();

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(notificationRepository.countByUser_Id(user.getId())).isEqualTo(1);
        assertThat(updatedUser.getLastUvNotifiedDate()).isEqualTo(LocalDate.of(2026, 6, 19));
    }
}