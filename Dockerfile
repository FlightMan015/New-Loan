FROM adoptopenjdk/maven-openjdk11 as builder
COPY pom.xml  /home/app/
COPY src/main/avro /home/app/src/main/avro
COPY src/main/resources /home/app/src/main/resources
WORKDIR /home/app
RUN mvn -q dependency:resolve
COPY src/ /home/app/src/
RUN mvn -q jacoco:prepare-agent verify jacoco:report


# ----------------------------------------------------------------------------------------------------------------------
FROM adoptopenjdk/openjdk11-openj9:alpine-jre
RUN apk add curl jq bash
COPY --from=builder /home/app/target/*.jar /app.jar
# Target folder is being copied because in jenkins file, coverage files are copied after running the image
COPY --from=builder /home/app/target /target
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]
