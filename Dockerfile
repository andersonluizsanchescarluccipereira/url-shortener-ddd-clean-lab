FROM docker.io/library/maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM docker.io/library/eclipse-temurin:21-jre
WORKDIR /opt/app
RUN useradd --system --uid 10001 --no-create-home shortener
COPY --from=build --chown=10001:10001 /build/target/url-shortener-ddd-clean-1.0.0-SNAPSHOT.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
