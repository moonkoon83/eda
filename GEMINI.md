# GEMINI.md: 'eda' 프로젝트를 위한 AI 어시스턴트 컨텍스트

이 문서는 AI 어시스턴트가 이 저장소의 개발 작업을 이해하고 효과적으로 지원하기 위한 컨텍스트를 제공합니다.

## 프로젝트 개요

이것은 Gradle로 빌드된 Java 프로젝트이며, Spring Boot 프레임워크를 사용합니다. 프로젝트 이름은 `eda`이고, "EDA project for Spring Boot"라는 설명을 통해 **이벤트 기반 아키텍처(Event-Driven Architecture, EDA)** 로 설계되었음을 알 수 있습니다.

프로젝트의 주요 목표는 RESTful 웹 서비스 또는 마이크로서비스를 구축하는 것으로 보입니다.

### 핵심 기술

*   **언어:** Java 17
*   **프레임워크:** Spring Boot
*   **빌드 도구:** Gradle
*   **핵심 의존성:**
    *   `spring-boot-starter-web`: RESTful API 구축용.
    *   `spring-boot-starter-data-jpa`: Java Persistence API를 사용한 데이터베이스 연동용.
    *   `lombok`: 반복적인 Java 코드를 줄이기 위함.
*   **데이터베이스:**
    *   `h2database`: 인메모리 데이터베이스로, 주로 개발 및 테스트용으로 사용될 가능성이 높습니다.
    *   `mysql-connector-j`: MySQL 데이터베이스 연결을 위한 JDBC 드라이버.
*   **테스트 & 문서화:**
    *   `spring-boot-starter-test` (JUnit 5 포함): 단위 및 통합 테스트용.
    *   `spring-restdocs-mockmvc`: Spring MVC 테스트 프레임워크로 작성된 테스트에서 API 문서를 생성하기 위함.

## 빌드 및 실행

이 프로젝트는 Gradle 래퍼(`gradlew`)를 사용하므로, 로컬에 Gradle을 설치할 필요가 없습니다.

### 주요 명령어

*   **프로젝트 빌드:**
    ```bash
    ./gradlew build
    ```

*   **애플리케이션 실행:**
    ```bash
    ./gradlew bootRun
    ```
    애플리케이션이 시작되지만, 기능은 데이터베이스 설정에 따라 달라집니다 (다른 프로필이 지정되지 않으면 기본적으로 H2를 사용).

*   **테스트 실행:**
    ```bash
    ./gradlew test
    ```
    이 명령어는 `build/generated-snippets`에 REST 문서용 스니펫도 생성합니다.

*   **API 문서 생성:**
    이 프로젝트는 Asciidoctor를 사용하여 문서를 생성하도록 설정되어 있습니다.
    ```bash
    ./gradlew asciidoctor
    ```
    이 작업은 필요한 스니펫을 만들기 위해 `test` 작업에 의존합니다.

## 개발 규칙

*   **코드 스타일:** Lombok의 존재는 깔끔하고 간결한 엔티티 및 모델 클래스를 선호함을 시사합니다. 표준 Java 및 Spring Boot 규칙을 따라야 합니다.
*   **설정:** 애플리케이션 속성은 `src/main/resources/application.yaml`에서 관리됩니다. 내용이 최소한인 것을 감안할 때, 애플리케이션은 Spring Boot의 자동 설정에 의존할 가능성이 높으며, 환경별 프로필이나 외부 설정을 통해 구성되도록 의도되었을 수 있습니다.
*   **API 우선 (문서 기반):** Spring REST Docs가 포함된 것은 잘 문서화된 API에 중점을 둔다는 것을 의미합니다. 새로운 엔드포인트에는 해당하는 문서 테스트가 함께 제공되어야 할 것입니다.

## 질문 가이드라인

**여기에 AI 어시스턴트에게 질문할 때 따르고 싶은 가이드라인을 작성해주세요.**

* 답변은 무조건 한글
* doc 폴더 밑에 날짜별로 파일을 생성해서 시간을 남겨주고 물어본 내용과 답변을 정리해서 저장해줘
* 제안해 준 내용은 바로 적용하지 말고 설명을 포함에서 보여주고 번호로 선택할수 있게 해줘

---

## Docker를 사용한 MySQL 설정 (docker-compose)

`docker` 디렉토리의 `docker-compose.yml` 파일을 사용하여 MySQL 데이터베이스를 실행합니다. 이 방법을 사용하면 데이터가 영구적으로 보존됩니다.

### 컨테이너 실행

프로젝트 루트 디렉토리에서 다음 스크립트를 실행합니다.
```bash
./run-db.sh
```
이 스크립트는 `docker/docker-compose.yml` 파일을 사용하여 MySQL 컨테이너를 백그라운드에서 시작합니다. 컨테이너가 시작되면 `root` 사용자의 비밀번호는 `root1234`이고, `eda` 스키마가 자동으로 생성됩니다. 데이터는 `mysql-data`라는 Docker 볼륨에 저장되어 컨테이너가 삭제되어도 유지됩니다.

### 컨테이너 중지

`docker` 디렉토리에서 다음 명령어를 실행합니다.
```bash
docker-compose down
```
이 명령어는 컨테이너를 중지하고 삭제하지만, `volumes:`에 정의된 `mysql-data` 볼륨은 삭제하지 않으므로 데이터는 보존됩니다.
