FROM openjdk:25-jdk-slim
COPY target/hospitalManagement-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]