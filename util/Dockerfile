FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем локально собранный JAR
COPY --from=build /app/target/*.jar app.jar

# Создаем пользователя
RUN addgroup -S springgroup && adduser -S springuser -G springgroup
USER springuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]