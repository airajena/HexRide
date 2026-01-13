FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/matching-service-1.0.0-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
