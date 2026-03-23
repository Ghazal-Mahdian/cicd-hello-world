FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/hello-world-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
