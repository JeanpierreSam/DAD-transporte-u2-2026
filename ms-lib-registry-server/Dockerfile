FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9761

ENTRYPOINT ["java", "-jar", "app.jar"]
