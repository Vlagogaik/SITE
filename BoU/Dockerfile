FROM maven:3-openjdk-17 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package

FROM openjdk:17-jdk-slim

WORKDIR /

#COPY --from=build /target/*.jar /app.jar
COPY  /target/*.jar /app.jar
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
