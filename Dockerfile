FROM azul/zulu-openjdk-alpine:17-latest

COPY gradlew .

COPY gradle gradle

COPY build.gradle .

COPY settings.gradle .

COPY src src

RUN sed -i 's/\r$//' gradlew

RUN chmod +x ./gradlew

RUN ./gradlew build --exclude-task test

RUN cp ./build/libs/*.jar ./app.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=prod" ,"/app.jar"]