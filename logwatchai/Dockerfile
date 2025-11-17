# Dockerfile für LogWatchAI

# 1. Basisimage mit Java
FROM eclipse-temurin:25-jdk-focal

# 2. Arbeitsverzeichnis
WORKDIR /app

# 3. Maven build (optional, wenn du nur JAR kopierst)
#COPY target/logwatchai-0.0.1-SNAPSHOT.jar app.jar

# 4. Kopiere JAR ins Image
COPY target/logwatchai-0.0.1-SNAPSHOT.jar app.jar

# 5. Port, auf dem Spring Boot läuft
EXPOSE 8080

# 6. Startbefehl
ENTRYPOINT ["java","-jar","app.jar"]
