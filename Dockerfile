FROM openjdk:18-alpine
EXPOSE 8080
COPY /build/libs/*.jar app.jar
ENTRYPOINT ["java","-XX:+UseSerialGC","-Xss512k","-jar","/app.jar"]