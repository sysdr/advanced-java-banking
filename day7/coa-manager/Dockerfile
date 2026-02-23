# Use a slim OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the executable JAR file from the Maven target directory
COPY target/coa-manager-1.0-SNAPSHOT.jar app.jar

# Expose any ports if your application needs them (not strictly needed for CLI)
# EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
