FROM openjdk:8-jre-alpine

MAINTAINER "http://personal-spa.herokuapp.com"

WORKDIR /opt/app
COPY build/libs/scraper.jar .

ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7005"

EXPOSE 8080 5005
EXPOSE 7005 7005

ENTRYPOINT exec java ${JAVA_OPTS} -jar "/opt/app/scraper.jar"