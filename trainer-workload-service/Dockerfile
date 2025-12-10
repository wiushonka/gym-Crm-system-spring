FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn -B -e -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar workload.jar


EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["sh","-c","exec java ${JAVA_OPTS} -jar workload.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]