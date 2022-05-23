FROM openjdk:11-jre-slim

MAINTAINER d.gavriil@yahoo.gr

ENV SPRING_PROFILES_ACTIVE dev

VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
RUN mkdir -p /logs

ENTRYPOINT ["java","-cp","app:app/lib/*","workable.movierama.MovieRamaApplication"]
