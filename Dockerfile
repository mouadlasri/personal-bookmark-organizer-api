FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy the Maven wrapper and pom first so downloaded dependencies can remain
# cached when only application source code changes.
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline

# Copy and compile the source only after the dependency layer is prepared.
COPY src src

RUN ./mvnw clean package -DskipTests


# The final image only needs a Java runtime and the packaged application.
# Maven, the JDK compiler, and source files remain in the builder stage.
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# EXPOSE documents the application's default container port. The actual host
# port is published with docker run -p, and platforms may provide PORT instead.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
