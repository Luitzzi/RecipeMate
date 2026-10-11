# ==== BUILD ====
FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /app

# Install dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Build app
COPY src ./src
RUN mvn -B package -DskipTests

# ==== RUNTIME ====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]
EXPOSE 8080
