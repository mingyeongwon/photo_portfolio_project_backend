FROM azul/zulu-openjdk-alpine:17-latest

# libwebp-tools 설치하여 `cwebp` 바이너리 추가
RUN apk add --no-cache libwebp-tools

RUN which cwebp

# 작업 디렉터리 설정
WORKDIR /app

# Step 2: WebP 변환 작업 준비
# WebP 변환 Java 파일 및 이미지 복사
COPY WebPConverter.java /app/
COPY input.png /app/input.png

# WebP 변환 Java 파일 컴파일
RUN javac WebPConverter.java


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
ENTRYPOINT ["java", "-Xmx1024m", "-Xms512m", "-jar", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "app.jar"]

