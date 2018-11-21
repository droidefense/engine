FROM maven:3.6.0-jdk-12-alpine as compiler

WORKDIR /opt
COPY . .
RUN mvn clean compile install -T4