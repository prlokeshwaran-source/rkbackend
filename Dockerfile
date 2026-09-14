FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/target/rksolutions-1.0.0.jar app.jar

RUN mkdir -p /data

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
