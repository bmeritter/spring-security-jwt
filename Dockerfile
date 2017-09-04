FROM openjdk:8-alpine

MAINTAINER sjyuan <sjyuan@thoughtworks.com>

COPY supervisor.conf /app/supervisor.conf

COPY build/libs/spring-security-jwt-*.jar /app/spring-security-jwt-*.jar

WORKDIR /app

CMD java -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -verbose:gc -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -Xloggc:gc_cdm.log -jar spring-security-jwt-*.jar
