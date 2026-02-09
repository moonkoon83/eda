#!/bin/bash
# 스크립트 실행 중 오류가 발생하면 즉시 중단합니다.
set -e

echo "🛑 로컬 DB 컨테이너를 중지하고 삭제합니다..."
docker-compose -f $(dirname "$0")/../docker/docker-compose.yml down

echo "✅ 로컬 DB 컨테이너가 중지되었습니다."
