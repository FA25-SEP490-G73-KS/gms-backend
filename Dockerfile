# syntax=docker/dockerfile:1

# ===== Build stage =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -q -e -B -DskipTests package

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Add a non-root user for security
RUN useradd -ms /bin/bash spring
USER spring

# Copy the fat jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# JVM and Spring Boot default opts can be overridden at runtime
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=default

# Health-friendly start command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]