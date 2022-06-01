FROM docker.io/gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM docker.io/openjdk:11-jre-slim
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/blablacar-notifier.jar
ENTRYPOINT ["java","-jar","/app/blablacar-notifier.jar"]
