# syntax=docker/dockerfile:1
# Muss zu <java.version> in deiner pom.xml passen (21 oder 25)
ARG JAVA_VERSION=21
# ---------- Stage 1: bauen ----------
FROM eclipse-temurin:${JAVA_VERSION}-jdk AS build
WORKDIR /workspace
# 1. Maven Wrapper und pom.xml: ändern sich selten
COPY mvnw pom.xml ./
COPY .mvn .mvn
# Windows-Zeilenenden entfernen und ausführbar machen
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
# Bibliotheken laden: läuft nur neu, wenn sich die pom.xml ändert
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q dependency:go-offline
# 2. Quellcode: ändert sich oft
COPY src src
# 3. Bauen, ohne Tests. Das lokale Maven-Repository liegt in einem Cache,
#    der zwischen Builds erhalten bleibt, aber nicht im Image landet.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package
# 4. JAR in Schichten zerlegen
RUN cp target/*.jar application.jar \
 && java -Djarmode=tools -jar application.jar extract --layers --destination extracted
# ---------- Stage 2: laufen ----------
FROM eclipse-temurin:${JAVA_VERSION}-jre AS runtime
# eigener Benutzer ohne root-Rechte
RUN groupadd --system quiz && useradd --system --gid quiz --no-create-home quiz
WORKDIR /app
# vom seltensten zum häufigsten Wechsel
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./
USER quiz
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "application.jar"]