# Use OpenJDK 21 as the base image for building
FROM openjdk:21-jdk-slim as build

# Set the working directory inside the container
WORKDIR /app

COPY pom.xml .

COPY src ./src

# Run Maven or Gradle to build the application
RUN ./mvnw clean install -DskipTests # For Maven
# RUN ./gradlew build -x test # For Gradle (use this if you are using Gradle)

# Use OpenJDK 21 as the base image for the runtime environment
FROM openjdk:21-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Find the JAR file dynamically by searching for *.jar in the target directory (Maven) or build/libs (Gradle)
# This assumes only one JAR file will be generated in the target/build/libs folder
COPY --from=build /app/target/*.jar /app/loan_calculator-0.0.1-SNAPSHOT.jar
# COPY --from=build /app/build/libs/*.jar /app/loan-calculator.jar  # For Gradle

# Expose the port that your app will run on (e.g., 8080)
EXPOSE 7123

# Command to run the app
ENTRYPOINT ["java", "-jar", "/app/loan_calculator-0.0.1-SNAPSHOT.jar"]