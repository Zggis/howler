FROM amazoncorretto:17.0.8-al2023-headless
EXPOSE 8080
COPY /build/libs/*.jar app.jar
ENTRYPOINT ["java","-XX:+UseSerialGC","-Xss512k","-Xms64m", "-Xmx128m","-jar","-Dspring.profiles.active=docker","/app.jar"]