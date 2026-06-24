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

* `2026.06.16. - 2026.06.21.`

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
<img width="1448" height="1086" alt="610786467-b855b01b-91fb-4c82-b752-4aa518651ce5" src="https://github.com/user-attachments/assets/dd776483-f5e7-455a-a018-bf518189f2f8" />
