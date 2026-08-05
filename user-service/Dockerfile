# ---- build stage ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew :user-service:bootJar -x test --no-daemon

# plain jar(-plain.jar, 실행 불가)와 bootJar가 같이 나오므로, 실행 가능한 bootJar만 골라서 고정 경로로 복사
RUN find user-service/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# ---- runtime stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/app.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
