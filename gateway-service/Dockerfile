FROM maven:3.9.4-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src/ src/

RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine

RUN adduser -S gateway -u 1001

WORKDIR /app

COPY --from=build --chown=fraud:fraud /app/target/*.jar gateway-service.jar

RUN chmod 444 gateway-service.jar

EXPOSE 8080
USER gateway

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
