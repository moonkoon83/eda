#!/bin/bash
set -e

# 프로젝트 루트로 이동
cd "$(dirname "$0")/.."

echo "🚀 Step 1: Building JAR files..."
bash scripts/build-jars.sh

echo "📦 Step 2: Building Docker images for each service..."

# JAR 파일 경로를 찾는 함수 (plain.jar 제외)
get_jar_path() {
    ls $1/build/libs/*.jar | grep -v "plain" | head -n 1
}

# 1. Order Service
ORDER_JAR=$(get_jar_path eda-order-service)
echo "Building order-service:latest using $ORDER_JAR..."
docker build --build-arg JAR_FILE=$ORDER_JAR -t order-service:latest .

# 2. Stock Service
STOCK_JAR=$(get_jar_path eda-stock-service)
echo "Building stock-service:latest using $STOCK_JAR..."
docker build --build-arg JAR_FILE=$STOCK_JAR -t stock-service:latest .

# 3. Point Service
POINT_JAR=$(get_jar_path eda-point-service)
echo "Building point-service:latest using $POINT_JAR..."
docker build --build-arg JAR_FILE=$POINT_JAR -t point-service:latest .

echo "✅ All images are built successfully!"
docker images | grep -E "order-service|stock-service|point-service"
