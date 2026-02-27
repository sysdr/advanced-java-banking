# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the compiled classes into the container at /app
COPY target/com /app/com

# Run with classpath /app so com.bank.ledger.* is found
CMD ["java", "-cp", "/app", "com.bank.ledger.LedgerSimulator"]
