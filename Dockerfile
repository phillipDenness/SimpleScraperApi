FROM openjdk:8-jdk-alpine

MAINTAINER "http://personal-spa.herokuapp.com"

WORKDIR /opt/app
COPY build/libs/scraper.jar .

ENTRYPOINT exec java -jar "/opt/app/scraper.jar"

RUN wget https://chromedriver.storage.googleapis.com/2.37/chromedriver_linux64.zip
RUN unzip chromedriver_linux64.zip
RUN mv chromedriver /usr/local/bin/chromedriver