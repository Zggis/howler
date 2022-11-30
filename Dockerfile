FROM amazoncorretto:11-alpine3.16
EXPOSE 8080
COPY /build/libs/*.jar app.jar
ENTRYPOINT ["java","-XX:+UseSerialGC","-Xss512k","-jar","/app.jar"]