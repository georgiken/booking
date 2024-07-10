# Используем официальный образ OpenJDK в качестве базового
FROM openjdk:17-jdk-alpine

LABEL authors="georg"

WORKDIR /booking

COPY target/booking-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
