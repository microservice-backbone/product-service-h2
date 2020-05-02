FROM openjdk:8-jre-alpine

# ARG can be override on build-step
ARG DEPLOYMENT_HOME=/usr/local

# ENV can be override on run-step
ENV DEPLOYMENT_HOME=${DEPLOYMENT_HOME}
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS
ENV JAVA_OPTS=""

RUN echo ${DEPLOYMENT_HOME}
RUN echo ${SPRING_OUTPUT_ANSI_ENABLED}

# deploy the fat-jar
COPY build/libs/*.jar ${DEPLOYMENT_HOME}/

EXPOSE 8080

# only 1 CMD line can be added
# mysqld
# apachectl -D FOREGROUND
# /bin/sh
CMD java ${JAVA_OPTS} -jar ${DEPLOYMENT_HOME}/*.jar

