# Stage 1: Build the JAR
FROM gradle:8.5-jdk21 AS builder
WORKDIR /home/gradle/project

# Copy Gradle files first to cache dependencies
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle gradle

# Download dependencies (cached)
RUN ./gradlew build --no-daemon --parallel

# Copy source code and build the JAR
COPY src src
RUN ./gradlew clean build -x test

# Stage 2: Run the JAR
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Expose port and run the JAR
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
