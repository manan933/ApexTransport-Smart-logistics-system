# ==========================================
# Stage 1: Build the Application with Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy POM and download dependencies for fast caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Production JRE Lightweight Runtime
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy compiled JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Environment defaults
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
