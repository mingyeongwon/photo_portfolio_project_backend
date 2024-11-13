FROM azul/zulu-openjdk-alpine:17-latest

# 필요한 시스템 라이브러리 설치
RUN apk add --no-cache libwebp-tools

WORKDIR /app

# 앱 파일 복사 및 권한 설정
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew

# 환경 변수 설정
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"
ENV PORT=8080

# 이미지 처리 관련 Java 시스템 프로퍼티 설정
ENV JAVA_OPTS="-Dawt.headless=true -Djava.awt.headless=true -Djavax.imageio.spi.debug=true"

# 빌드 및 실행
RUN ./gradlew build --exclude-task test
RUN cp ./build/libs/portfolio_project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "app.jar"]