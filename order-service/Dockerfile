FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY ./base-domains ./base-domains
RUN mvn -f ./base-domains/pom.xml clean install -DskipTests

COPY ./order-service ./order-service
RUN mvn -f ./order-service/pom.xml clean package -DskipTests -Pdocker

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/order-service/target/order-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]