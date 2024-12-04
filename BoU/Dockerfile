FROM maven:3-openjdk-17 AS build
#EXPOSE 8081
#ADD target/BoU-0.0.1-SNAPSHOT.jar BoU-0.0.1-SNAPSHOT.jar
#ENTRYPOINT ["java", "-jar", "/BoU-0.0.1-SNAPSHOT.jar.jar"]
#ENTRYPOINT
WORKDIR /opt/app
COPY mvnw pom.xml ./
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /opt/app
EXPOSE 8081
COPY --from=build /opt/app/target/*.jar /opt/app.jar
#COPY  /target/BoU-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar"]
