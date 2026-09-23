# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd -r app && useradd -r -g app app
COPY --from=build /app/target/*.jar app.jar
RUN chown -R app:app /app
USER app

EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-jar","app.jar"]