# Use a slim OpenJDK base image
FROM eclipse-temurin:11-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the Maven build output (relative to build context = day6)
COPY LedgerSystem/target/ledger-system-1.0-SNAPSHOT.jar /app/app.jar

# Define the command to run your application
CMD ["java", "-jar", "app.jar"]
