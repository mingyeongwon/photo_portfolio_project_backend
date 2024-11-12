# Base image: Java 17 on Alpine Linux
FROM azul/zulu-openjdk-alpine:17-latest

# Install cwebp (webp package) for image processing
RUN apk add --no-cache findutils webp

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

# Build the Spring Boot application
RUN ./gradlew build --exclude-task test
RUN ls -la ./build/libs/

RUN cp ./build/libs/portfolio_project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "app.jar"]
