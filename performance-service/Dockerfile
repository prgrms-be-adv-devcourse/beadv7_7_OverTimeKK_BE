# 빌드 컨텍스트는 반드시 레포 루트여야 함 (멀티모듈이라 common 등을 같이 복사해야 함)
# 실행 예: docker build -f performance-service/Dockerfile -t performance-service .

# ---- build stage ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew :performance-service:bootJar -x test --no-daemon

# plain jar(-plain.jar, 실행 불가)와 bootJar가 같이 나오므로, 실행 가능한 bootJar만 골라서 고정 경로로 복사
RUN find performance-service/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/app.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
