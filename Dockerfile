# Build Stage
FROM maven:3.9.8-eclipse-temurin-21-alpine as builder
WORKDIR /app
COPY . .
RUN mvn package -Dmaven.test.skip=true

# Final Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY  --from=builder /app/target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]