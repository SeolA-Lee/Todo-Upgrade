FROM openjdk:21
ARG JAR_FILE=./build/libs/todolist-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY ./src/main/resources/application-secret.yml application-secret.yml
ENTRYPOINT ["java", "-jar", "/app.jar"]