FROM gradle:8.4.0-jdk17 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle testswithme-backend:shadowJar --no-daemon

FROM openjdk:17
EXPOSE 8080 8443
RUN mkdir /app /data /app-data
COPY --from=build /home/gradle/src/testswithme-backend/build/libs/testswithme-backend.jar /app/testswithme-backend.jar
ENTRYPOINT ["java", "-jar", "/app/testswithme-backend.jar"]
