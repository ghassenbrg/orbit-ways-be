FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY ./target/orbitways-1.0.0-SNAPSHOT.jar orbitways.jar
COPY ./src/main/resources/application.properties /app/config/application.properties
EXPOSE 8080
CMD ["java", "-jar", "orbitways.jar", "--spring.config.location=/app/config/application.properties"]