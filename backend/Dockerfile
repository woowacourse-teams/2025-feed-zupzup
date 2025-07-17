FROM openjdk:21-jdk

ARG PROFILE
ENV PROFILE=${PROFILE}

COPY build/libs/*SNAPSHOT.jar /feed-zupzup-server.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "feed-zupzup-server.jar"]
