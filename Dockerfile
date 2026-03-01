# Stage 1 — Build the JAR
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /project
COPY . .
RUN chmod +x gradlew && ./gradlew -p app clean bootJar --no-daemon

# Stage 2 — Run the JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /project/app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]