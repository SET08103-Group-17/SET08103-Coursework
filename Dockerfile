# Use OpenJDK base image
FROM openjdk:latest
# Copy JAR file with all dependencies into /tmp directory in the container
COPY ./target/SET08103-Coursework.jar /tmp
# Set working directory to /tmp
WORKDIR /tmp
# Define entry point to run the JAR file using java
ENTRYPOINT ["java", "-jar", "SET08103-Coursework.jar", "db:3306", "10000"]