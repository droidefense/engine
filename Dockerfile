FROM java:8u111-jdk as compiler

ARG MAVEN_VERSION=3.6.0
ARG USER_HOME_DIR="/root"
ARG SHA=fae9c12b570c3ba18116a4e26ea524b29f7279c17cbaadc3326ca72927368924d9131d11b9e851b8dc9162228b6fdea955446be41207a5cfc61283dd8a561d2f
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# RUN apt-get install oracle-java8-unlimited-jce-policy

WORKDIR /opt
COPY . .
# RUN mvn -Prelease clean compile install && ls -alh /opt/dist/release/ && java -jar /opt/dist/release/droidefense-jar-with-dependencies.jar
RUN mvn -Prelease clean compile package && \
	mv /opt/dist/release/droidefense-jar-with-dependencies.jar /opt/dist/release/droidefense.jar && \
	ls -alh /opt/dist/release/ && \
	java -jar /opt/dist/release/droidefense.jar && \


FROM openjdk:8u181 as runner

MAINTAINER kernel@droidefense.com

WORKDIR /opt
COPY --from=compiler /opt/dist/release/droidefense.jar ./droidefense.jar
CMD ["java", "-jar", "./droidefense.jar"]