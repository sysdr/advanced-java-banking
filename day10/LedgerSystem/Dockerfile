# Use an official Eclipse Temurin (OpenJDK) runtime as a parent image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the compiled classes into the container
COPY build /app/build
COPY src/main/java/com/ledger/app/LedgerApp.java /app/src/main/java/com/ledger/app/LedgerApp.java
COPY src/main/java/com/ledger/core/Account.java /app/src/main/java/com/ledger/core/Account.java
COPY src/main/java/com/ledger/core/SynchronizedAccountService.java /app/src/main/java/com/ledger/core/SynchronizedAccountService.java
COPY src/main/java/com/ledger/app/TransactionWorker.java /app/src/main/java/com/ledger/app/TransactionWorker.java


# Set the command to run the application
CMD ["java", "-cp", "./build", "com.ledger.app.LedgerApp"]
