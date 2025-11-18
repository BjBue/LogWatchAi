# Dockerfile for LogWatchAI

# 1. Basisimage Java
FROM eclipse-temurin:25-jdk

# 2. workdirectory
WORKDIR /app

# 3. copy JAR in Image
COPY target/logwatchai-0.0.1-SNAPSHOT.jar app.jar

# 4. copy yaml config
COPY config/logwatchai.yaml /app/config/logwatchai.yaml

# 5. Port for api-server
EXPOSE 8080

# 6. Startcommand
ENTRYPOINT ["java","-jar","app.jar"]
