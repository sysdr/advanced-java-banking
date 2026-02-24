# Use a slim Java runtime image
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

COPY target/LedgerCanonicalSerializer-1.0-SNAPSHOT.jar /app/LedgerCanonicalSerializer-1.0-SNAPSHOT.jar
COPY target/lib /app/lib

ENTRYPOINT ["java", "-jar", "LedgerCanonicalSerializer-1.0-SNAPSHOT.jar"]
