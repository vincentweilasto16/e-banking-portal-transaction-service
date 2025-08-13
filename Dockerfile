# Use a lightweight OpenJDK image
FROM openjdk:17-jdk-slim

# Optional: create a volume for logs
VOLUME /tmp

# Copy the built JAR into the container
ARG JAR_FILE=target/transaction-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app.jar"]