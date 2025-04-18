# Stage 1: Build the JAR
FROM gradle:7.6.1-jdk21 AS builder

# Set the working directory and copy the project files into the container
WORKDIR /home/gradle/project
COPY . .

# Make sure gradlew is executable
RUN chmod +x gradlew

# Run the Gradle build
RUN ./gradlew build --no-daemon

# Stage 2: Run the JAR
FROM openjdk:21-jdk-slim AS runtime

# Set the working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Define the command to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
