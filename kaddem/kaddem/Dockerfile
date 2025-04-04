FROM openjdk:17-jdk-slim
COPY target/kaddem-0.0.1-RELEASE.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]