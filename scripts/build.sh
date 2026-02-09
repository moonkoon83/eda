#!/bin/bash
set -e

# 스크립트가 위치한 디렉토리의 부모 디렉토리(프로젝트 루트)로 이동합니다.
# 이렇게 하면 이후의 모든 명령어는 프로젝트 루트에서 실행됩니다.
cd "$(dirname "$0")/.."

# --- 1. 애플리케이션 빌드 ---
echo "✅ Step 1: Spring Boot 애플리케이션을 빌드합니다... (./gradlew build -x test)"
./gradlew build -x test

# --- 2. Docker 이미지 빌드 ---
# 이제 빌드 컨텍스트가 프로젝트 루트이므로, 경로를 간단하게 지정할 수 있습니다.
echo -e "\n✅ Step 2: Docker 이미지를 빌드합니다... (docker build -t eda-app:latest .)"
docker build -t eda-app:latest .

echo -e "\n🚀 빌드가 완료되었습니다."
echo "   이미지 이름: eda-app:latest"
echo "   (참고) 원격 레지스트리에 푸시하려면: docker push <your-registry>/eda-app:latest"
