# Use OpenJDK base image
FROM openjdk:latest
# Copy JAR file with all dependencies into /tmp directory in the container
COPY ./target/SET08103-Coursework-0.1.0.1-jar-with-dependencies.jar /tmp
# Set working directory to /tmp
WORKDIR /tmp
# Define entry point to run the JAR file using java
ENTRYPOINT ["java", "-jar", "SET08103-Coursework-0.1.0.1-jar-with-dependencies.jar"]