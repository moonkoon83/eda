#!/bin/bash
set -e

# 프로젝트 루트로 이동
cd "$(dirname "$0")/.."

echo "🔨 Building Service JARs (bootJar)..."
./gradlew :eda-order-service:bootJar :eda-stock-service:bootJar :eda-point-service:bootJar -x test

echo "✅ JAR files are built successfully!"
ls -l eda-*-service/build/libs/*.jar | grep -v "plain"
