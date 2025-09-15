FROM openjdk:21-jdk

ARG SPRING_ACTIVE_PROFILE
ENV PROFILE=${SPRING_ACTIVE_PROFILE}

COPY otel/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
COPY build/libs/*SNAPSHOT.jar /feed-zupzup-server.jar

ENTRYPOINT ["java", \
    "-javaagent:/otel/opentelemetry-javaagent.jar", \
    "-Dspring.profiles.active=${PROFILE}", \
    "-jar", "feed-zupzup-server.jar"]
