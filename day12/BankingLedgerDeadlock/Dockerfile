# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled classes from the build directory
COPY build /app/build

# Copy the source files (optional, but good for debugging inside container)
COPY src /app/src

# Set the classpath to include the build directory
ENV CLASSPATH=/app/build

# Command to run the application (default to running all demos sequentially)
CMD java com.banking.ledger.demo.DeadlockDemo && 
    java com.banking.ledger.demo.DeadlockPreventedDemo && 
    java com.banking.ledger.demo.TryLockDemo
