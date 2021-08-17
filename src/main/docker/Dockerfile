# To build, run the following command from the top level project directory:
#
# docker build -t osirixfoundation/karnak:latest -f src/main/docker/Dockerfile .

# Based on build image containing maven, jdk and git
FROM adoptopenjdk:15-jre-hotspot as builder
ARG JAR_FILE=target/karnak*.jar
WORKDIR /app
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Build the final deployment image
FROM adoptopenjdk:15-jre-hotspot
WORKDIR app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
COPY tools/docker-entrypoint.sh .
RUN mkdir externalid-providers

EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]