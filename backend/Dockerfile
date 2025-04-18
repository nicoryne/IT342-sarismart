# Stage 1: Build the JAR
FROM gradle:8.5-jdk21 AS builder

# Set working directory
WORKDIR /home/gradle/project

# Copy all files to the container
COPY . .

# Make sure Gradle wrapper is executable
RUN chmod +x gradlew

# Build the Spring Boot app
RUN ./gradlew build --no-daemon

# Stage 2: Run the JAR
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Expose the app port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
