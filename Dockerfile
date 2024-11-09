FROM openjdk:17

COPY gradlew .

COPY gradle gradle

COPY build.gradle .

COPY settings.gradle .

COPY src src

RUN chmod +x ./gradlew

RUN ./gradlew build --exclude-task test

RUN cp ./build/libs/*.jar ./portfolio_project.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=prod" ,"/portfolio_project.jar"]