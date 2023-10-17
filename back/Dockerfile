FROM gradle:7-jdk11 AS build
COPY --chown=./back/gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/soloteam.jar
ENTRYPOINT ["java","-jar","/app/soloteam.jar"]