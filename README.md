# 🐣 계란주의보 Backend

<img src="https://github.com/user-attachments/assets/25bb66aa-6864-491f-8d0b-a0a955c691c3" width="218" height="216" alt="계란주의보" />

> 계란주의보 Backend: 현재 위치의 자외선 지수와 날씨 정보를 기반으로 외출 세션, 선크림 도포 상태, 맞춤형 알림을 관리하는 iOS 앱 백엔드 API 서버입니다.

---

<br>

## 👥 멤버

| 카망 | 곤 | 사이먼 | 오웬 |
|:------:|:------:|:------:|:------:|
| <img src="https://github.com/HYUNJOON-SUNG.png" width="180" height="180" alt="카망" /> | <img src="https://github.com/taehyeon1998.png" width="180" height="180" alt="곤" /> | <img src="https://github.com/hoondongseo.png" width="180" height="180" alt="사이먼" /> | <img src="https://github.com/tjfdlvTkr.png" width="180" height="180" alt="오웬" /> |
| 성현준 | 김태현 | 서동훈 | 신준하 |
| BE | BE | BE | BE |
| [GitHub](https://github.com/HYUNJOON-SUNG) | [GitHub](https://github.com/taehyeon1998) | [GitHub](https://github.com/hoondongseo) | [GitHub](https://github.com/tjfdlvTkr) |

<br>


## 📱 소개

계란주의보는 사용자의 현재 위치를 기준으로 날씨와 자외선 지수를 조회하고, 외출 중 선크림 도포 상태를 귀여운 계란 캐릭터 상태로 보여주는 서비스입니다.

백엔드는 iOS 클라이언트에서 필요한 인증, 회원가입, 외출 세션 관리, 선크림 재도포 기록, 날씨/자외선 조회, 알림 조회 기능을 제공합니다.

<br>

## 📆 프로젝트 기간

* 전체 기간: `2026.06.16. - 2026.06.21.`
* 개발 기간: `2026.06.16. - 2026.06.21.`

<br>

## 🤔 요구사항

For building and running the application you need:

Java 21
Spring Boot 3.5.8
Gradle Wrapper
MySQL
Docker

<br>

## 🏗 Architecture

* Spring Boot 기반 REST API 서버
* Domain 단위 패키지 분리 구조
* Controller - Service - Repository 계층 분리
* JPA 기반 데이터베이스 연동
* JWT 기반 인증 처리
* Kakao 로그인 기반 인증 플로우
* OpenFeign 기반 외부 API 연동
* OpenWeather API 기반 날씨/자외선 정보 조회
* VWorld API 기반 위치 정보 처리
* 공통 응답, 예외 처리, 보안 설정 분리

<br>

## 🧩 시스템 아키텍처


<p align="center">
<img width="1448" height="1086" alt="610786467-b855b01b-91fb-4c82-b752-4aa518651ce5" src="https://github.com/user-attachments/assets/00cbed17-9045-4d19-903b-a457104db714" />
</p>

<br>

## 🗂 ERD


<p align="center">
<img width="1721" height="728" alt="erd" src="https://github.com/user-attachments/assets/8748edff-5eb3-4b40-9df0-7f0bd467087a" />
</p>

<br>

## 🔎 기술 스택

### 🖥 Development

<div align="left">
<img src="https://img.shields.io/badge/Java_21-007396?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/Spring_Boot_3.5.8-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
<img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
<img src="https://img.shields.io/badge/Spring_Validation-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
<img src="https://img.shields.io/badge/OpenFeign-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
</div>

<br>

### 🗄 Database

<div align="left">
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
<img src="https://img.shields.io/badge/H2_Test-09476B?style=for-the-badge&logo=h2database&logoColor=white" />
</div>

<br>

### 🔐 Auth & API Docs

<div align="left">
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" />
</div>

<br>

### ⚙ Infra

<div align="left">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" />
</div>

<br>

## ✨ 주요 기능

### 🔐 Auth

* Kakao 로그인 기반 인증 세션 생성
* 테스트용 인증 세션 생성
* JWT Access Token 재발급
* 현재 로그인 세션 로그아웃

<br>

### 👤 User

* 회원가입
* 사용자 기본 정보 저장
* 인증 플로우와 연동되는 사용자 관리

<br>

### 🌤 Weather

* 현재 위치 기반 날씨 정보 조회
* 현재 위치 기반 자외선 지수 조회
* 외부 API 응답을 앱에서 사용하기 쉬운 형태로 변환

<br>

### 🚶 Outing

* 외출 세션 생성
* 진행 중인 외출 세션 조회
* 선크림 재도포 기록
* 외출 세션 종료 및 상태 갱신

<br>

### 🔔 Notification

* 사용자 알림 목록 조회
* 알림 읽음 처리
* 외출/자외선 상태 기반 알림 제공

<br>

## 📡 API

| Domain       | Method | Endpoint                                                 | Description           |
| :----------- | :----: | :------------------------------------------------------- | :-------------------- |
| Auth         |  POST  | `/api/v1/auth-sessions`                                  | Kakao 로그인 기반 인증 세션 생성 |
| Auth         |  POST  | `/api/v1/auth-sessions/test`                             | 테스트용 인증 세션 생성         |
| Auth         |  POST  | `/api/v1/auth-tokens`                                    | JWT 토큰 재발급            |
| Auth         | DELETE | `/api/v1/auth-sessions/current`                          | 현재 세션 로그아웃            |
| User         |  POST  | `/api/v1/users`                                          | 회원가입                  |
| AppLaunch    |  POST  | `/api/v1/app-launches`                                   | 앱 실행 시 외출 흐름 조회       |
| Weather      |   GET  | `/api/v1/weather-observations`                           | 위치 기반 날씨/자외선 정보 조회    |
| Outing       |  POST  | `/api/v1/outing-sessions`                                | 외출 세션 생성              |
| Outing       |   GET  | `/api/v1/outing-sessions/current`                        | 진행 중인 외출 세션 조회        |
| Outing       |  POST  | `/api/v1/outing-sessions/current/sunscreen-applications` | 선크림 재도포 기록            |
| Outing       |  PATCH | `/api/v1/outing-sessions/{outingSessionId}`              | 외출 세션 종료/수정           |
| Notification |   GET  | `/api/v1/notifications`                                  | 읽지 않은 알림 목록 조회        |
| Notification |  PATCH | `/api/v1/notifications/{notificationId}`                 | 알림 읽음 처리              |

<br>

## 📁 프로젝트 구조

```bash
src/main/java/com/umc10th/umc10th_hackathon_team_b_be
├── domain
│   ├── applaunch
│   │   ├── controller
│   │   ├── dto
│   │   └── service
│   ├── auth
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── repository
│   │   └── service
│   ├── notification
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── enums
│   │   ├── repository
│   │   └── service
│   ├── outing
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── enums
│   │   ├── repository
│   │   └── service
│   ├── user
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── enums
│   │   ├── repository
│   │   └── service
│   └── weather
│       ├── client
│       ├── controller
│       ├── dto
│       ├── enums
│       └── service
└── global
    ├── config
    ├── exception
    ├── response
    └── security
```

<br>

## 🔧 Environment Variables

`.env.example`을 참고하여 `.env` 파일을 생성합니다.

```env
SPRING_PROFILES_ACTIVE=local

DB_HOST=
DB_PORT=3306
DB_NAME=umc10th_hackathon_team_b_be
DB_USERNAME=root
DB_PASSWORD=

JWT_SECRET=

KAKAO_REST_API_KEY=
OPENWEATHER_API_KEY=
VWORLD_API_KEY=

SUBMISSION_TEST_ENABLED=true
```

<br>

## 🚀 실행 방법

### 1. Repository Clone

```bash
git clone https://github.com/UMC10th-hackathon-team-B/UMC10th-hackathon-team-B-BE.git
cd UMC10th-hackathon-team-B-BE
```

<br>

### 2. 환경 변수 설정

```bash
cp .env.example .env
```

`.env` 파일에 DB 정보와 외부 API Key를 입력합니다.

<br>

### 3. Build

```bash
./gradlew clean build
```

<br>

### 4. Run

```bash
./gradlew bootRun
```

<br>

## 🐳 Docker 실행

### 1. Build

```bash
./gradlew clean build
docker build -t egg-alert-be .
```

<br>

### 2. Run

```bash
docker run -p 8080:8080 --env-file .env egg-alert-be
```

<br>

## 📘 API Docs

서버 실행 후 아래 주소에서 Swagger 문서를 확인할 수 있습니다.

```bash
http://localhost:8080/swagger-ui/index.html
```

<br>

## 📝 Git Convention

### Branch

| Branch | Description |
|:--|:--|
| `main` | 운영 배포 브랜치 |
| `develop` | 개발 통합 브랜치 |
| `feat/*` | 기능 개발 |
| `fix/*` | 버그 수정 |
| `docs/*` | 문서 수정 |
| `style/*` | 코드 포맷팅 |
| `refactor/*` | 코드 구조 개선 |
| `chore/*` | 설정, 빌드, 기타 작업 |
| `hotfix/*` | 운영 긴급 수정 |

<br>

### Commit Message

```bash
type: subject
````

```bash
feat: 회원가입 API 추가
fix: 로그인 예외 처리 수정
docs: API 문서 수정
style: 코드 포맷팅 정리
refactor: 인증 서비스 구조 개선
chore: CI/CD 설정 추가
hotfix: 운영 배포 오류 긴급 수정
```

<br>

### Commit Type

| Type       | Description        |
| :--------- | :----------------- |
| `feat`     | 새로운 기능 추가          |
| `fix`      | 버그 수정              |
| `docs`     | 문서 추가 또는 수정        |
| `style`    | 로직 변경 없는 코드 스타일 수정 |
| `refactor` | 기능 변경 없는 코드 구조 개선  |
| `chore`    | 설정, 빌드, 기타 작업      |
| `hotfix`   | 운영 긴급 수정           |

<br>

### Merge Rule

```bash
feat/*, fix/*, docs/*, style/*, refactor/*, chore/* -> develop
hotfix/* -> main, develop
develop -> main
```

`main` 브랜치에는 직접 작업하지 않고, 개발 작업은 `develop` 브랜치에서 분기하여 진행합니다.


## 🌿 Branch

```bash
main
develop
feat/*
```

<br>

## 📌 Repository

* Backend: `UMC10th-hackathon-team-B-BE`
* Frontend: iOS Swift 기반 계란주의보 앱
