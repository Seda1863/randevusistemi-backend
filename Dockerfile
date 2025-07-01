FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy certs
COPY src/main/resources/certs/ /app/certs/

# Set default environment variables
# These can be overridden at runtime with docker run -e
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://pine-software-backend_pinesoftware:5432/randevusistemi?createDatabaseIfNotExist=true
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=2d7bfa8a7f80b6698ebd
ENV SPRING_HIBERNATE_DDL=create-drop

ENV JWT_PUBLIC_KEY=file:/app/certs/public.pem
ENV JWT_PRIVATE_KEY=file:/app/certs/private.pem
ENV SERVER_PORT=8080

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]