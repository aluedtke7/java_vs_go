FROM openjdk:11-jre-slim-buster
VOLUME /tmp
COPY ./config.yml config.yml
COPY target/dropwizard-*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","server","config.yml"]
