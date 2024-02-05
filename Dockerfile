FROM --platform=linux/arm64 azul/zulu-openjdk-alpine:21 as builder
ARG JAR_FILE=target/delivery-microservice-0.9.0-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM azul/zulu-openjdk-alpine:21
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
COPY .env.docker.fake ./BOOT-INF/classes/.env
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher", "-XX:MaxRAMPercentage=75", "-XX:+UseG1GC"]