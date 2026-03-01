# Stage 1 — Build the JAR
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY app/ .
RUN gradle bootJar --no-daemon

# Stage 2 — Run the JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]