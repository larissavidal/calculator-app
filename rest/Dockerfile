# Stage 1: Build com Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn clean install -DskipTests

# Stage 2: Imagem final com JDK
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /build/target/rest-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]