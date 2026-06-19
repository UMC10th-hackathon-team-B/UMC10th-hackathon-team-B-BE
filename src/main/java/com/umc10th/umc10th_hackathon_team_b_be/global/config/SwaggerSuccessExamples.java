package com.umc10th.umc10th_hackathon_team_b_be.global.config;

public final class SwaggerSuccessExamples {

    public static final String AUTH_SESSION_HOME = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "nextScreen": "HOME",
                "userId": 1,
                "auth": {
                  "tokenType": "Bearer",
                  "accessToken": "access-token-example",
                  "accessTokenExpiresInSeconds": 1800,
                  "refreshToken": "refresh-token-example",
                  "refreshTokenExpiresAt": "2026-07-18T09:00:00"
                }
              }
            }
            """;

    public static final String AUTH_SESSION_TERMS = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "nextScreen": "TERMS",
                "signupToken": "signup-token-example",
                "signupTokenExpiresInSeconds": 600
              }
            }
            """;

    public static final String AUTH_TOKEN_REISSUE = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "tokenType": "Bearer",
                "accessToken": "new-access-token-example",
                "accessTokenExpiresInSeconds": 1800,
                "refreshToken": "new-refresh-token-example",
                "refreshTokenExpiresAt": "2026-07-18T09:00:00"
              }
            }
            """;

    public static final String LOGOUT = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": null
            }
            """;

    public static final String USER_SIGNUP = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "nextScreen": "HOME",
                "userId": 1,
                "auth": {
                  "tokenType": "Bearer",
                  "accessToken": "access-token-example",
                  "accessTokenExpiresInSeconds": 1800,
                  "refreshToken": "refresh-token-example",
                  "refreshTokenExpiresAt": "2026-07-18T09:00:00"
                }
              }
            }
            """;

    public static final String WEATHER_OBSERVATION = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "weather": {
                  "locationName": "송파구 문정동",
                  "weatherType": "CLEAR",
                  "weatherLabel": "맑음",
                  "temperatureCelsius": 24.6,
                  "uvIndex": 7.2,
                  "uvLevel": "HIGH",
                  "uvLevelLabel": "높음"
                },
                "egg": {
                  "eggStatus": "SAFE",
                  "eggStatusLabel": "안전한 계란",
                  "message": "오늘의 자외선을 확인해볼까요?"
                },
                "outingStart": {
                  "canStart": true
                },
                "notification": {
                  "unreadCount": 2
                }
              }
            }
            """;

    public static final String OUTING_FLOW_HOME = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "nextScreen": "HOME",
                "home": {
                  "weather": {
                    "locationName": "송파구 문정동",
                    "weatherType": "CLEAR",
                    "weatherLabel": "맑음",
                    "temperatureCelsius": 24.6,
                    "uvIndex": 7.2,
                    "uvLevel": "HIGH",
                    "uvLevelLabel": "높음"
                  },
                  "egg": {
                    "eggStatus": "SAFE",
                    "eggStatusLabel": "안전한 계란",
                    "message": "오늘의 자외선을 확인해볼까요?"
                  },
                  "outingStart": {
                    "canStart": true
                  },
                  "notification": {
                    "unreadCount": 1
                  }
                }
              }
            }
            """;

    public static final String OUTING_FLOW_OUTING = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "nextScreen": "OUTING",
                "outing": {
                  "outingSession": {
                    "outingSessionId": 10,
                    "startedAt": "2026-06-18T09:10:00",
                    "autoEndAt": "2026-06-18T20:00:00",
                    "elapsedMinutes": 75,
                    "elapsedTimeText": "1시간 15분"
                  },
                  "weather": {
                    "locationName": "송파구 문정동",
                    "weatherType": "CLEAR",
                    "weatherLabel": "맑음",
                    "temperatureCelsius": 24.6,
                    "uvIndex": 7.2,
                    "uvLevel": "HIGH",
                    "uvLevelLabel": "높음"
                  },
                  "egg": {
                    "eggStatus": "LIGHT_TOASTED",
                    "eggStatusLabel": "살짝 노릇한 계란",
                    "message": "햇빛에 살짝 익고 있어요"
                  },
                  "sunscreen": {
                    "lastSunscreenAppliedAt": "2026-06-18T09:55:00",
                    "lastSunscreenAppliedElapsedMinutes": 30,
                    "lastSunscreenAppliedText": "30분 전 마지막 기록"
                  },
                  "notification": {
                    "unreadCount": 2
                  }
                }
              }
            }
            """;

    public static final String OUTING_FLOW_ENDED = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "nextScreen": "HOME",
                "endedSession": {
                  "outingSessionId": 10,
                  "status": "COMPLETED",
                  "endReason": "MANUAL",
                  "startedAt": "2026-06-18T09:10:00",
                  "endedAt": "2026-06-18T10:30:00",
                  "elapsedMinutes": 80,
                  "elapsedTimeText": "1시간 20분"
                }
              }
            }
            """;

    public static final String OUTING_FLOW_AUTO_ENDED = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "nextScreen": "HOME",
                "endedSession": {
                  "outingSessionId": 10,
                  "status": "COMPLETED",
                  "endReason": "AUTO",
                  "startedAt": "2026-06-18T09:10:00",
                  "endedAt": "2026-06-18T20:00:00",
                  "elapsedMinutes": 650,
                  "elapsedTimeText": "10시간 50분"
                },
                "autoEndNotice": {
                  "showPopup": true,
                  "title": "자외선 관리 시간이 종료됐어요",
                  "message": "저녁 8시 이후에는 외출 모드가 자동으로 종료돼요. 외출 기록을 저장하고 홈 모드로 이동할게요."
                }
              }
            }
            """;

    public static final String NOTIFICATION_LIST = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "unreadCount": 2,
                "notifications": [
                  {
                    "notificationId": 31,
                    "type": "EGG_DANGER",
                    "title": "자외선 차단제를 발라주세요!",
                    "content": "계란이가 많이 익었어요. 자외선 노출에 주의해주세요.",
                    "createdAt": "2026-06-18T10:25:00",
                    "createdTimeText": "오전 10:25",
                    "isRead": false
                  },
                  {
                    "notificationId": 30,
                    "type": "DAILY_UV",
                    "title": "오늘 자외선 지수가 높아요",
                    "content": "외출 전 모자나 선글라스를 챙기고 차단제를 꼼꼼히 발라주세요.",
                    "createdAt": "2026-06-18T09:10:00",
                    "createdTimeText": "오전 09:10",
                    "isRead": false
                  }
                ]
              }
            }
            """;

    public static final String NOTIFICATION_EMPTY = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "unreadCount": 0,
                "notifications": [],
                "emptyMessage": "아직 확인할 알림이 없어요."
              }
            }
            """;

    public static final String NOTIFICATION_READ = """
            {
              "success": true,
              "code": "COMMON_200",
              "message": "요청에 성공했습니다.",
              "data": {
                "unreadCount": 1,
                "notifications": [
                  {
                    "notificationId": 30,
                    "type": "DAILY_UV",
                    "title": "오늘 자외선 지수가 높아요",
                    "content": "외출 전 모자나 선글라스를 챙기고 차단제를 꼼꼼히 발라주세요.",
                    "createdAt": "2026-06-18T09:10:00",
                    "createdTimeText": "오전 09:10",
                    "isRead": false
                  }
                ]
              }
            }
            """;

    private SwaggerSuccessExamples() {
    }
}
