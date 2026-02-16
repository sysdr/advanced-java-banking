# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container's filesystem
COPY target/banking-ledger-day2-1.0-SNAPSHOT.jar app.jar

# Expose any ports if your application listens on them (not applicable for this CLI app)
# EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "app.jar"]
