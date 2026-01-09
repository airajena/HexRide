FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy all project files
COPY pom.xml .
COPY common common
COPY eureka-server eureka-server
COPY api-gateway api-gateway
COPY user-service user-service
COPY driver-service driver-service
COPY ride-service ride-service
COPY location-service location-service
COPY matching-service matching-service
COPY websocket-service websocket-service

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests -pl eureka-server -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
