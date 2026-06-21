package com.umc10th.umc10th_hackathon_team_b_be.domain.outing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.umc10th.umc10th_hackathon_team_b_be.domain.notification.service.NotificationService;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingFlowResponse;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionCreateRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.OutingSessionEndRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.dto.SunscreenApplicationRequest;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.entity.OutingSession;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.EggStatus;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionEndReason;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.OutingSessionStatus;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.enums.SunscreenAppliedOption;
import com.umc10th.umc10th_hackathon_team_b_be.domain.outing.repository.OutingSessionRepository;
import com.umc10th.umc10th_hackathon_team_b_be.domain.user.entity.User;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.BusinessException;
import com.umc10th.umc10th_hackathon_team_b_be.global.exception.ErrorCode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutingService {

    private static final LocalTime OUTING_START_TIME = LocalTime.of(5, 0);
    private static final LocalTime AUTO_END_TIME = LocalTime.of(20, 0);
    private static final LocalTime AUTO_END_NOTICE_END_TIME = LocalTime.of(5, 0);
    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final BigDecimal EGG_DANGER_NOTIFICATION_THRESHOLD = BigDecimal.valueOf(170);
    private static final String OUTING_UNAVAILABLE_MESSAGE = "저녁 8시 이후에는 외출 모드를 시작할 수 없어요.";
    private static final String AUTO_END_NOTICE_TITLE = "자외선 관리 시간이 종료됐어요";
    private static final String AUTO_END_NOTICE_MESSAGE =
            "저녁 8시 이후에는 외출 모드가 자동으로 종료돼요. 외출 기록을 저장하고 홈 모드로 이동할게요.";
    private static final ConcurrentHashMap<Long, ReentrantLock> USER_OPERATION_LOCKS = new ConcurrentHashMap<>();

    private final OutingSessionRepository outingSessionRepository;
    private final OutingWeatherProvider outingWeatherProvider;
    private final NotificationService notificationService;
    private final EntityManager entityManager;
    private final Clock clock;
    private final TransactionTemplate transactionTemplate;

    public OutingFlowResponse handleAppLaunch(Long userId, double latitude, double longitude) {
        OutingFlowResponse autoEndResponse = completeExpiredCurrentSessionIfNeeded(userId);
        if (autoEndResponse != null) {
            return autoEndResponse;
        }

        OutingFlowResponse.WeatherResponse weather = getWeather(latitude, longitude);

        return executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            Optional<OutingSession> currentSession = findCurrentSessionForUpdate(userId);

            if (currentSession.isPresent()) {
                OutingSession outingSession = currentSession.get();
                if (shouldAutoEnd(outingSession, now)) {
                    return autoEndAndBuildResponse(outingSession, now);
                }

                createDailyUvNotificationIfNeeded(userId, weather);
                return refreshAndBuildOutingResponse(outingSession, weather, now);
            }

            createDailyUvNotificationIfNeeded(userId, weather);
            return OutingFlowResponse.home(new OutingFlowResponse.HomeResponse(
                    weather,
                    homeEggResponse(),
                    outingStartResponse(now),
                    notificationSummary(userId)
            ));
        });
    }

    public OutingFlowResponse createSession(Long userId, OutingSessionCreateRequest request) {
        OutingFlowResponse currentSessionResponse = checkCurrentSessionBeforeCreate(userId);
        if (currentSessionResponse != null) {
            return currentSessionResponse;
        }

        OutingFlowResponse.WeatherResponse weather = getWeather(request.latitude(), request.longitude());

        return executeWithUserOperationLock(userId, status -> {
            User user = getUserForUpdate(userId);
            LocalDateTime now = now();
            Optional<OutingSession> currentSession = findCurrentSessionForUpdate(userId);
            if (currentSession.isPresent()) {
                OutingSession outingSession = currentSession.get();
                if (shouldAutoEnd(outingSession, now)) {
                    return autoEndAndBuildResponse(outingSession, now);
                }
                throw new BusinessException(ErrorCode.OUTING_409);
            }

            validateOutingStartTime(now);
            LocalDateTime lastSunscreenAppliedAt = resolveLastSunscreenAppliedAt(
                    request.sunscreenAppliedOption(),
                    now
            );
            EggCalculation eggCalculation = calculateEgg(
                    lastSunscreenAppliedAt,
                    now,
                    weather.uvIndex()
            );

            OutingSession outingSession = OutingSession.start(
                    user,
                    weather.uvIndex(),
                    eggCalculation.score(),
                    eggCalculation.status(),
                    lastSunscreenAppliedAt,
                    now,
                    now.toLocalDate().atTime(AUTO_END_TIME)
            );
            outingSessionRepository.save(outingSession);

            return OutingFlowResponse.outing(buildOutingResponse(outingSession, weather, now));
        });
    }

    public OutingFlowResponse getCurrentSession(Long userId, double latitude, double longitude) {
        OutingFlowResponse precheckResponse = checkCurrentSessionBeforeRefresh(userId);
        if (precheckResponse != null) {
            return precheckResponse;
        }

        OutingFlowResponse.WeatherResponse weather = getWeather(latitude, longitude);

        return executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            OutingSession outingSession = findCurrentSessionForUpdateOrThrow(userId);
            if (shouldAutoEnd(outingSession, now)) {
                return autoEndAndBuildResponse(outingSession, now);
            }

            return refreshAndBuildOutingResponse(outingSession, weather, now);
        });
    }

    public OutingFlowResponse applySunscreen(Long userId, SunscreenApplicationRequest request) {
        OutingFlowResponse precheckResponse = checkCurrentSessionBeforeRefresh(userId);
        if (precheckResponse != null) {
            return precheckResponse;
        }

        OutingFlowResponse.WeatherResponse weather = getWeather(request.latitude(), request.longitude());

        return executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            OutingSession outingSession = findCurrentSessionForUpdateOrThrow(userId);
            if (shouldAutoEnd(outingSession, now)) {
                return autoEndAndBuildResponse(outingSession, now);
            }

            outingSession.recordSunscreenApplication(now);
            return refreshAndBuildOutingResponse(outingSession, weather, now);
        });
    }

    public OutingFlowResponse completeSession(Long userId, Long outingSessionId, OutingSessionEndRequest request) {
        return executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            OutingSession outingSession = findCurrentSessionForUpdateOrThrow(userId);

            if (shouldAutoEnd(outingSession, now)) {
                return autoEndAndBuildResponse(outingSession, now);
            }
            if (!outingSession.getId().equals(outingSessionId)) {
                throw new BusinessException(ErrorCode.OUTING_404);
            }
            if (request.status() != OutingSessionStatus.COMPLETED) {
                throw new BusinessException(ErrorCode.OUTING_400);
            }

            outingSession.complete(OutingSessionEndReason.MANUAL, now);
            return OutingFlowResponse.ended(buildEndedSessionResponse(outingSession));
        });
    }

    public OutingFlowResponse completeExpiredCurrentSessionIfNeeded(Long userId) {
        return executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            Optional<OutingSession> currentSession = findCurrentSessionForUpdate(userId);
            if (currentSession.isEmpty() || !shouldAutoEnd(currentSession.get(), now)) {
                return null;
            }
            return autoEndAndBuildResponse(currentSession.get(), now);
        });
    }

    public void completeCurrentSessionForLogout(Long userId) {
        executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            findCurrentSessionForUpdate(userId).ifPresent(outingSession -> {
                if (shouldAutoEnd(outingSession, now)) {
                    outingSession.complete(OutingSessionEndReason.AUTO, outingSession.getAutoEndAt());
                    return;
                }
                outingSession.complete(OutingSessionEndReason.LOGOUT, now);
            });
            return null;
        });
    }

    private OutingFlowResponse checkCurrentSessionBeforeCreate(Long userId) {
        return executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            Optional<OutingSession> currentSession = findCurrentSessionForUpdate(userId);
            if (currentSession.isEmpty()) {
                validateOutingStartTime(now);
                return null;
            }

            OutingSession outingSession = currentSession.get();
            if (shouldAutoEnd(outingSession, now)) {
                return autoEndAndBuildResponse(outingSession, now);
            }
            throw new BusinessException(ErrorCode.OUTING_409);
        });
    }

    private OutingFlowResponse checkCurrentSessionBeforeRefresh(Long userId) {
        return executeWithUserOperationLock(userId, status -> {
            getUserForUpdate(userId);
            LocalDateTime now = now();
            OutingSession outingSession = findCurrentSessionForUpdateOrThrow(userId);
            if (shouldAutoEnd(outingSession, now)) {
                return autoEndAndBuildResponse(outingSession, now);
            }
            return null;
        });
    }

    private OutingFlowResponse refreshAndBuildOutingResponse(
            OutingSession outingSession,
            OutingFlowResponse.WeatherResponse weather,
            LocalDateTime now
    ) {
        BigDecimal previousEggScore = outingSession.getEggScore();
        EggCalculation eggCalculation = calculateEgg(
                outingSession.getLastSunscreenAppliedAt(),
                now,
                weather.uvIndex()
        );

        outingSession.updateEggCalculation(
                eggCalculation.score(),
                eggCalculation.status(),
                weather.uvIndex(),
                now
        );
        createEggDangerNotificationIfNeeded(outingSession, previousEggScore, eggCalculation.score());

        return OutingFlowResponse.outing(buildOutingResponse(outingSession, weather, now));
    }

    private void createEggDangerNotificationIfNeeded(
            OutingSession outingSession,
            BigDecimal previousEggScore,
            BigDecimal currentEggScore
    ) {
        if (previousEggScore.compareTo(EGG_DANGER_NOTIFICATION_THRESHOLD) < 0
                && currentEggScore.compareTo(EGG_DANGER_NOTIFICATION_THRESHOLD) >= 0) {
            notificationService.createEggDanger(outingSession);
        }
    }

    private void createDailyUvNotificationIfNeeded(Long userId, OutingFlowResponse.WeatherResponse weather) {
        notificationService.createDailyUvIfNeeded(userId, weather.uvIndex().doubleValue());
    }

    private OutingFlowResponse autoEndAndBuildResponse(OutingSession outingSession, LocalDateTime now) {
        outingSession.complete(OutingSessionEndReason.AUTO, outingSession.getAutoEndAt());
        return OutingFlowResponse.autoEnded(
                buildEndedSessionResponse(outingSession),
                new OutingFlowResponse.AutoEndNoticeResponse(
                        shouldShowAutoEndPopup(outingSession, now),
                        AUTO_END_NOTICE_TITLE,
                        AUTO_END_NOTICE_MESSAGE
                )
        );
    }

    private OutingFlowResponse.OutingResponse buildOutingResponse(
            OutingSession outingSession,
            OutingFlowResponse.WeatherResponse weather,
            LocalDateTime now
    ) {
        return new OutingFlowResponse.OutingResponse(
                new OutingFlowResponse.OutingSessionSummaryResponse(
                        outingSession.getId(),
                        outingSession.getStartedAt(),
                        outingSession.getAutoEndAt(),
                        elapsedMinutes(outingSession.getStartedAt(), now),
                        elapsedTimeText(elapsedMinutes(outingSession.getStartedAt(), now))
                ),
                weather,
                outingEggResponse(outingSession.getEggStatus()),
                sunscreenResponse(outingSession, now),
                notificationSummary(outingSession.getUser().getId())
        );
    }

    private OutingFlowResponse.EndedSessionResponse buildEndedSessionResponse(OutingSession outingSession) {
        long elapsedMinutes = elapsedMinutes(outingSession.getStartedAt(), outingSession.getEndedAt());
        return new OutingFlowResponse.EndedSessionResponse(
                outingSession.getId(),
                outingSession.getStatus(),
                outingSession.getEndReason(),
                outingSession.getStartedAt(),
                outingSession.getEndedAt(),
                elapsedMinutes,
                elapsedTimeText(elapsedMinutes)
        );
    }

    private OutingFlowResponse.SunscreenResponse sunscreenResponse(
            OutingSession outingSession,
            LocalDateTime now
    ) {
        LocalDateTime lastAppliedAt = outingSession.getLastSunscreenAppliedAt();
        if (lastAppliedAt == null) {
            return new OutingFlowResponse.SunscreenResponse(null, null, "차단제 기록 없음");
        }

        long elapsedMinutes = elapsedMinutes(lastAppliedAt, now);
        return new OutingFlowResponse.SunscreenResponse(
                lastAppliedAt,
                elapsedMinutes,
                sunscreenElapsedText(elapsedMinutes)
        );
    }

    private OutingFlowResponse.EggResponse homeEggResponse() {
        return new OutingFlowResponse.EggResponse(
                EggStatus.SAFE,
                eggStatusLabel(EggStatus.SAFE),
                "오늘의 자외선을 확인해볼까요?"
        );
    }

    private OutingFlowResponse.EggResponse outingEggResponse(EggStatus eggStatus) {
        return new OutingFlowResponse.EggResponse(
                eggStatus,
                eggStatusLabel(eggStatus),
                eggStatusMessage(eggStatus)
        );
    }

    private OutingFlowResponse.OutingStartResponse outingStartResponse(LocalDateTime now) {
        boolean canStart = isOutingStartAvailable(now);
        return new OutingFlowResponse.OutingStartResponse(
                canStart,
                canStart ? null : OUTING_UNAVAILABLE_MESSAGE
        );
    }

    private OutingFlowResponse.NotificationSummaryResponse notificationSummary(Long userId) {
        return new OutingFlowResponse.NotificationSummaryResponse(notificationService.getUnreadCount(userId));
    }

    private EggCalculation calculateEgg(
            LocalDateTime lastSunscreenAppliedAt,
            LocalDateTime now,
            BigDecimal uvIndex
    ) {
        boolean hasSunscreenApplied = lastSunscreenAppliedAt != null;
        long sunscreenElapsedMinutes = hasSunscreenApplied ? elapsedMinutes(lastSunscreenAppliedAt, now) : 0;

        BigDecimal score = sunscreenRiskScore(hasSunscreenApplied, sunscreenElapsedMinutes)
                .multiply(uvWeight(uvIndex))
                .setScale(2, RoundingMode.HALF_UP);

        return new EggCalculation(score, eggStatus(score));
    }

    private BigDecimal sunscreenRiskScore(boolean hasSunscreenApplied, long sunscreenElapsedMinutes) {
        if (!hasSunscreenApplied) {
            return BigDecimal.valueOf(120);
        }
        if (sunscreenElapsedMinutes <= 15) {
            return BigDecimal.TEN;
        }
        if (sunscreenElapsedMinutes <= 30) {
            return BigDecimal.valueOf(15);
        }
        if (sunscreenElapsedMinutes <= 60) {
            return BigDecimal.valueOf(35);
        }
        if (sunscreenElapsedMinutes <= 120) {
            return BigDecimal.valueOf(60);
        }
        return BigDecimal.valueOf(95);
    }

    private BigDecimal uvWeight(BigDecimal uvIndex) {
        if (uvIndex.compareTo(BigDecimal.valueOf(3)) < 0) {
            return BigDecimal.valueOf(0.5);
        }
        if (uvIndex.compareTo(BigDecimal.valueOf(6)) < 0) {
            return BigDecimal.valueOf(1.0);
        }
        if (uvIndex.compareTo(BigDecimal.valueOf(8)) < 0) {
            return BigDecimal.valueOf(1.5);
        }
        return BigDecimal.valueOf(2.0);
    }

    private EggStatus eggStatus(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(30)) <= 0) {
            return EggStatus.SAFE;
        }
        if (score.compareTo(BigDecimal.valueOf(70)) <= 0) {
            return EggStatus.LIGHT_TOASTED;
        }
        if (score.compareTo(BigDecimal.valueOf(120)) <= 0) {
            return EggStatus.TOASTED;
        }
        if (score.compareTo(BigDecimal.valueOf(180)) <= 0) {
            return EggStatus.BURNED;
        }
        return EggStatus.DANGER;
    }

    private String eggStatusLabel(EggStatus eggStatus) {
        return switch (eggStatus) {
            case SAFE -> "안전한 계란";
            case LIGHT_TOASTED -> "살짝 노릇한 계란";
            case TOASTED -> "노릇한 계란";
            case BURNED -> "탄 계란";
            case DANGER -> "위험한 계란";
        };
    }

    private String eggStatusMessage(EggStatus eggStatus) {
        return switch (eggStatus) {
            case SAFE -> "차단제 덕분에 뽀얀 계란이에요.";
            case LIGHT_TOASTED -> "차단제 효과가 조금씩 줄어들고 있어요.";
            case TOASTED -> "차단제가 슬슬 필요해요.";
            case BURNED -> "계란이가 타기 전에 덧발라요.";
            case DANGER -> "계란이를 차단제로 구해줘요.";
        };
    }

    private OutingFlowResponse.WeatherResponse getWeather(double latitude, double longitude) {
        try {
            return validateWeatherResponse(outingWeatherProvider.getCurrentWeather(latitude, longitude));
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }
    }

    private OutingFlowResponse.WeatherResponse validateWeatherResponse(OutingFlowResponse.WeatherResponse weather) {
        if (weather == null
                || isBlank(weather.locationName())
                || isBlank(weather.weatherType())
                || isBlank(weather.weatherLabel())
                || weather.temperatureCelsius() == null
                || weather.uvIndex() == null
                || isBlank(weather.uvLevel())
                || isBlank(weather.uvLevelLabel())
                || weather.uvIndex().compareTo(BigDecimal.ZERO) < 0
                || weather.uvIndex().compareTo(BigDecimal.valueOf(99.99)) > 0
                || weather.temperatureCelsius().compareTo(BigDecimal.valueOf(-100)) < 0
                || weather.temperatureCelsius().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException(ErrorCode.WEATHER_502);
        }
        return weather;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void validateOutingStartTime(LocalDateTime now) {
        if (!isOutingStartAvailable(now)) {
            throw new BusinessException(ErrorCode.OUTING_400, OUTING_UNAVAILABLE_MESSAGE);
        }
    }

    private boolean isOutingStartAvailable(LocalDateTime now) {
        LocalTime currentTime = now.toLocalTime();
        return !currentTime.isBefore(OUTING_START_TIME) && currentTime.isBefore(AUTO_END_TIME);
    }

    private boolean shouldAutoEnd(OutingSession outingSession, LocalDateTime now) {
        return !now.isBefore(outingSession.getAutoEndAt());
    }

    private boolean shouldShowAutoEndPopup(OutingSession outingSession, LocalDateTime now) {
        LocalDateTime popupEndAt = outingSession.getAutoEndAt()
                .toLocalDate()
                .plusDays(1)
                .atTime(AUTO_END_NOTICE_END_TIME);

        return !now.isBefore(outingSession.getAutoEndAt()) && now.isBefore(popupEndAt);
    }

    private LocalDateTime resolveLastSunscreenAppliedAt(
            SunscreenAppliedOption sunscreenAppliedOption,
            LocalDateTime now
    ) {
        if (!sunscreenAppliedOption.hasAppliedTime()) {
            return null;
        }
        return now.minusMinutes(sunscreenAppliedOption.getMinutesBeforeStart());
    }

    private OutingSession findCurrentSessionForUpdateOrThrow(Long userId) {
        return findCurrentSessionForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OUTING_404));
    }

    private Optional<OutingSession> findCurrentSessionForUpdate(Long userId) {
        List<OutingSession> sessions = outingSessionRepository.findCurrentSessionsByUserIdForUpdate(
                userId,
                OutingSessionStatus.IN_PROGRESS
        );
        return sessions.stream().findFirst();
    }

    private User getUserForUpdate(Long userId) {
        User user = entityManager.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_404);
        }
        return user;
    }

    private <T> T executeWithUserOperationLock(Long userId, TransactionCallback<T> callback) {
        ReentrantLock lock = USER_OPERATION_LOCKS.computeIfAbsent(userId, key -> new ReentrantLock());
        lock.lock();
        try {
            return transactionTemplate.execute(callback);
        } finally {
            lock.unlock();
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.ofInstant(clock.instant(), SEOUL_ZONE_ID).withNano(0);
    }

    private long elapsedMinutes(LocalDateTime start, LocalDateTime end) {
        return Math.max(0, Duration.between(start, end).toMinutes());
    }

    private String elapsedTimeText(long elapsedMinutes) {
        if (elapsedMinutes < 60) {
            return elapsedMinutes + "분";
        }
        return elapsedMinutes / 60 + "시간 " + elapsedMinutes % 60 + "분";
    }

    private String sunscreenElapsedText(long elapsedMinutes) {
        if (elapsedMinutes == 0) {
            return "방금 전 마지막 기록";
        }
        return elapsedTimeText(elapsedMinutes) + " 전 마지막 기록";
    }

    private record EggCalculation(
            BigDecimal score,
            EggStatus status
    ) {
    }
}
