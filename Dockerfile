FROM --platform=linux/amd64 openjdk:19-jdk-alpine
VOLUME /tmp
COPY target/In-app-Currency-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]