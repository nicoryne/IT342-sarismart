# Stage 1: Build the JAR
FROM gradle:8.5-jdk21 AS builder
WORKDIR /home/gradle/project
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle gradle
COPY src src
RUN ./gradlew build --no-daemon --no-test

# Stage 2: Run the JAR
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
