# 1. 베이스 이미지 설정 (OpenJDK 기반)
FROM azul/zulu-openjdk-alpine:17-latest

# 2. libwebp-tools 설치 (cwebp 포함)
RUN apk add --no-cache libwebp-tools

# cwebp 경로 확인 (디버깅 용도)
RUN which cwebp

# 3. 작업 디렉터리 설정
WORKDIR /app

# 4. WebP 변환용 파일 복사 및 컴파일
COPY WebPConverter.java /app/
COPY input.png /tmp/input.png 
# 입력 파일은 Cloud Run에서 쓰기 가능한 /tmp로 복사
RUN javac WebPConverter.java

# 5. Gradle 빌드 도구 및 프로젝트 파일 복사
COPY gradlew . 
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Gradle 실행 권한 설정
RUN sed -i 's/\r$//' gradlew && chmod +x ./gradlew

# 6. Spring Boot 애플리케이션 빌드
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"
RUN ./gradlew build --exclude-task test

# 빌드된 JAR 파일 확인 (디버깅)
RUN ls -la ./build/libs/

# 빌드 결과물 복사
RUN cp ./build/libs/portfolio_project-0.0.1-SNAPSHOT.jar app.jar

# 7. 애플리케이션 실행 환경 설정
ENV PORT=8080
EXPOSE ${PORT}

# WebP 변환 및 Spring Boot 애플리케이션 실행
CMD ["java", "-Xmx1024m", "-Xms512m", "-jar", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "app.jar"]
