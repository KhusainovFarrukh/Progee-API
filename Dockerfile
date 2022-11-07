FROM openjdk:18-alpine
EXPOSE 8080
COPY target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]