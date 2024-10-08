FROM openjdk:latest
WORKDIR SET08103-Coursework
COPY ./target/SET08103-Coursework-1.0-SNAPSHOT-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "SET08103-Coursework-1.0-SNAPSHOT-jar-with-dependencies.jar"]