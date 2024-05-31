FROM amazoncorretto:17-alpine
COPY target/*-with-dependencies.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]