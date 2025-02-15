# Use Amazon Corretto 17 JDK as the base image
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application JAR file into the container
COPY target/*.jar app.jar

# Expose the port your Spring Boot application listens on (default is 8080)
EXPOSE 8080

# Set the default command to run the JAR file with Java
ENTRYPOINT ["java", "-jar", "app.jar"]