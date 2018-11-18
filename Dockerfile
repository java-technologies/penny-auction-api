FROM openjdk:8-jre-alpine

COPY build/libs/*.jar /service.jar

CMD java -jar /service.jar