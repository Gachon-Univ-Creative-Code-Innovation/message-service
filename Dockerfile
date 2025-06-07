## 테스트 용도
#FROM eclipse-temurin:17-jdk-alpine
#
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "/app.jar"]

# 1단계: 빌드
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -x test --no-daemon

# 2단계: 실행 (경량 이미지)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

