package com.umc10th.umc10th_hackathon_team_b_be.global.config;

public final class SwaggerErrorExamples {

    public static final String COMMON_400 = """
            {
              "success": false,
              "code": "COMMON_400",
              "message": "잘못된 요청입니다.",
              "data": null
            }
            """;

    public static final String AUTH_400 = """
            {
              "success": false,
              "code": "AUTH_400",
              "message": "유효하지 않은 로그인 요청입니다.",
              "data": null
            }
            """;

    public static final String AUTH_401 = """
            {
              "success": false,
              "code": "AUTH_401",
              "message": "인증이 필요합니다.",
              "data": null
            }
            """;

    public static final String USER_404 = """
            {
              "success": false,
              "code": "USER_404",
              "message": "사용자를 찾을 수 없습니다.",
              "data": null
            }
            """;

    public static final String TERMS_400 = """
            {
              "success": false,
              "code": "TERMS_400",
              "message": "필수 약관에 모두 동의해야 합니다.",
              "data": null
            }
            """;

    public static final String OUTING_400 = """
            {
              "success": false,
              "code": "OUTING_400",
              "message": "외출 세션을 시작할 수 없습니다.",
              "data": null
            }
            """;

    public static final String OUTING_404 = """
            {
              "success": false,
              "code": "OUTING_404",
              "message": "진행 중인 외출 세션이 없습니다.",
              "data": null
            }
            """;

    public static final String OUTING_409 = """
            {
              "success": false,
              "code": "OUTING_409",
              "message": "이미 진행 중인 외출 세션이 있습니다.",
              "data": null
            }
            """;

    public static final String NOTIFICATION_404 = """
            {
              "success": false,
              "code": "NOTIFICATION_404",
              "message": "알림을 찾을 수 없습니다.",
              "data": null
            }
            """;

    public static final String WEATHER_502 = """
            {
              "success": false,
              "code": "WEATHER_502",
              "message": "날씨 정보를 불러오지 못했습니다.",
              "data": null
            }
            """;

    private SwaggerErrorExamples() {
    }
}
