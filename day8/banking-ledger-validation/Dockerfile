FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/banking-ledger-validation-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
