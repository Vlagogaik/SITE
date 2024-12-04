FROM maven:3-openjdk-17 AS builder
#ARG JAR_FILE=target/*.jar
#COPY ./target/BoU-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java", "-jar","/app.jar"]

WORKDIR /
COPY mvnw pom.xml ./
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /
EXPOSE 8080
COPY --from=builder /target/*.jar /*.jar
COPY --from=builder /src/main/resources/templates /templates
COPY --from=builder /src/main/resources/static /static

ENTRYPOINT ["java", "-jar", "/*.jar"]
