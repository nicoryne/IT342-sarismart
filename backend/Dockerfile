# Stage 1: Build the JAR
FROM gradle:7.6.1-jdk17 AS builder
COPY . /home/gradle/project
WORKDIR /home/gradle/project
RUN ./gradlew build --no-daemon

# Stage 2: Run the JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
