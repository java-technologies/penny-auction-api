FROM openjdk:8-alpine

EXPOSE 8080

WORKDIR penny-auction-service

COPY . .

RUN ./gradlew clean assemble

ENTRYPOINT java -jar build/libs/*.jar


