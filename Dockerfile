# 아키텍처 호환성이 좋은 Ubuntu 기반 Temurin 이미지 사용
FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
