FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :server:buildFatJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/server/build/libs/server-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]