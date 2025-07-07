# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar base-domains completo y compilarlo
COPY ./base-domains ./base-domains
RUN mvn -f ./base-domains/pom.xml clean install -DskipTests

# Copiar el microservicio completo y compilarlo
COPY ./stock-service ./stock-service
RUN mvn -f ./stock-service/pom.xml clean install -DskipTests

# Etapa 2: Imagen final
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/stock-service/target/stock-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]