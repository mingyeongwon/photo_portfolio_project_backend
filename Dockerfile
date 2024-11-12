FROM azul/zulu-openjdk-alpine:17-latest

# cwebp 설치 (심볼릭 링크 생성을 제거)
RUN apk add --no-cache libwebp-tools

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"
ENV PORT=8080

# Spring Boot 애플리케이션 빌드
RUN ./gradlew build --exclude-task test
RUN ls -la ./build/libs/

RUN cp ./build/libs/portfolio_project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "app.jar"]
