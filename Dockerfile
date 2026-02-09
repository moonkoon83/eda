# 1. 빌드 스테이지: Gradle을 사용하여 애플리케이션을 빌드합니다.
FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /build

# 빌드에 필요한 파일들을 먼저 복사하여 Docker 레이어 캐시를 활용합니다.
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon

# 소스 코드를 복사합니다.
COPY src ./src

# Gradle 빌드를 실행합니다. (-x test는 테스트를 건너뛰어 빌드 속도를 높입니다.)
RUN ./gradlew build --no-daemon -x test

# 2. 실행 스테이지: 빌드된 애플리케이션을 실행할 가벼운 이미지를 만듭니다.
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 복사합니다.
# build/libs/*.jar 패턴을 사용하여 파일 이름이 변경되어도 동작하도록 합니다.
COPY --from=builder /build/build/libs/*.jar ./app.jar

# 애플리케이션 실행
# 기본 Spring 프로필을 'local'로 설정합니다.
# Kubernetes 환경 등에서 SPRING_PROFILES_ACTIVE 환경 변수가 설정되면 이 값은 재정의됩니다.
ENV SPRING_PROFILES_ACTIVE=local
ENTRYPOINT ["java", "-jar", "app.jar"]
