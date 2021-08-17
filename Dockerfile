# To build, run the following command from the top level project directory:
#
# docker build -t osirixfoundation/karnak:latest -f Dockerfile .

# Based on build image containing maven, jdk and git
FROM maven:3.6-adoptopenjdk-15 as builder
WORKDIR /app

# Build the Spring Boot application with layers
COPY pom.xml .
COPY src ./src
COPY frontend frontend
COPY externalid-provider-interface externalid-provider-interface
RUN mvn -B -f externalid-provider-interface/pom.xml install
RUN mvn -B package -P production
WORKDIR /app/bin
RUN cp ../target/karnak*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Build the final deployment image
FROM adoptopenjdk:15-jre-hotspot
WORKDIR app
COPY --from=builder /app/bin/dependencies/ ./
COPY --from=builder /app/bin/spring-boot-loader/ ./
COPY --from=builder /app/bin/snapshot-dependencies/ ./
COPY --from=builder /app/bin/application/ ./
COPY tools/docker-entrypoint.sh .
RUN mkdir externalid-providers

EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]
