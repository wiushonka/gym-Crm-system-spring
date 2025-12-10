FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn -B -e -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

COPY --from=builder /build/src/main/resources/trainerData.txt /app/resources/trainerData.txt
COPY --from=builder /build/src/main/resources/traineeData.txt /app/resources/traineeData.txt
COPY --from=builder /build/src/main/resources/trainingTypesData.txt /app/resources/trainingTypesData.txt

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["sh","-c","exec java ${JAVA_OPTS} -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]