FROM openjdk:11

# basic docker file, probably it won't work, but structure should be same

WORKDIR /src
COPY . /src

RUN set -ex \
  && ./mvnw clean install -Dmaven.test.skip=true

# need to replace project3 snapshot
CMD ["java", "-jar", "project3-0.0.1-SNAPSHOT.jar", "--server.port=8080"]