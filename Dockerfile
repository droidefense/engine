FROM maven:3.6.0-jdk-12-alpine as compiler

RUN apt-get install oracle-java8-unlimited-jce-policy

WORKDIR /opt
COPY . .
RUN mvn clean compile install -T4