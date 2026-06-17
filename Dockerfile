# Java 21 JDK가 설치된 Temurin 이미지를 기반 이미지로 사용
FROM eclipse-temurin:21-jdk

# 컨테이너 내부 작업 디렉토리를 /app으로 설정
WORKDIR /app

# Gradle 빌드 결과물 jar 경로를 빌드 인자로 지정
ARG JAR_FILE=build/libs/*SNAPSHOT.jar

# 빌드된 jar 파일을 컨테이너 내부 project.jar로 복사
COPY ${JAR_FILE} project.jar

# Spring Boot 애플리케이션이 컨테이너 내부에서 사용할 포트
EXPOSE 8080

# 컨테이너 시작 시 Spring Boot jar 실행
ENTRYPOINT ["java", "-jar", "project.jar"]