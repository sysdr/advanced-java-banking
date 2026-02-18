# Use Eclipse Temurin OpenJDK 11 JRE (openjdk:11-jre-slim is deprecated)
FROM eclipse-temurin:11-jre

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container's /app directory
COPY target/rounding-mastery-1.0-SNAPSHOT.jar app.jar

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
