FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY  target/notification-service-*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
