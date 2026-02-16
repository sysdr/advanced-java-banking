# Use an official OpenJDK runtime as a parent image (eclipse-temurin is widely available)
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the compiled classes into the container at /app
COPY out/ .

# Command to run the application
CMD ["java", "com.bank.ledger.MainApp"]
