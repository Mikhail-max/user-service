FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY target/gateway-service-*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
