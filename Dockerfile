#FROM openjdk:11
#
## basic docker file, probably it won't work, but structure should be same
#
#WORKDIR /src
#COPY . /src
#
#RUN set -ex \
#  && ./mvnw clean install -Dmaven.test.skip=true
#
## need to replace project3 snapshot
#CMD ["java", "-jar", "raft-0.0.1-SNAPSHOT.jar", "--server.port=8080"]

FROM maven:3.6.3-jdk-11 as target
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /build/src/
RUN mvn package

# Step : Package image
FROM openjdk:11
#EXPOSE 4567
CMD exec java $JAVA_OPTS -jar /app/my-app.jar
COPY --from=target /build/target/raft-0.0.1-SNAPSHOT.jar /app/my-app.jar

#FROM maven:3.6.3-jdk-11 AS build
#COPY src /usr/src/app/src
#COPY pom.xml /usr/src/app
#RUN mvn -f /usr/src/app/pom.xml clean package
#
#FROM openjdk:11
#COPY --from=build /usr/src/app/target/raft-1.0.0-SNAPSHOT.jar /usr/app/raft-1.0.0-SNAPSHOT.jar
##EXPOSE 8080
#ENTRYPOINT ["java","-jar","/usr/app/raft-1.0.0-SNAPSHOT.jar"]