# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Compila el jar
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- runtime stage ----------
# Opción segura y ligera (sin shell)
FROM gcr.io/distroless/java17-debian12

WORKDIR /app
# Copia el jar empacado por Spring Boot
COPY --from=build /app/target/*.jar /app/app.jar

# Buenas prácticas para JVM en contenedor
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -Dfile.encoding=UTF-8 -Duser.timezone=UTC"

# Puerto de la app
EXPOSE 8080

# Distroless corre como nonroot por defecto
ENTRYPOINT ["java","-jar","/app/app.jar"]
