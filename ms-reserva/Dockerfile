FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9082

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=postgresql"]
