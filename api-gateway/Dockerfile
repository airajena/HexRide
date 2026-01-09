FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy all project files
COPY pom.xml .
COPY common common
COPY api-gateway api-gateway
COPY user-service user-service
COPY driver-service driver-service
COPY ride-service ride-service
COPY location-service location-service
COPY matching-service matching-service
COPY eureka-server eureka-server
COPY websocket-service websocket-service

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests -pl common,api-gateway -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
