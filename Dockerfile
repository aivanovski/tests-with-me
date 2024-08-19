FROM gradle:8.4.0-jdk17 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle web-backend:shadowJar --no-daemon

FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/web-backend/build/libs/tests-with-me-backend.jar /app/tests-with-me-backend.jar
ENTRYPOINT ["java", "-jar", "/app/tests-with-me-backend.jar"]
