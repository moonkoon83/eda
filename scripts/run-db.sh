#!/bin/bash

# docker/docker-compose.yml 파일을 사용하여 MySQL 컨테이너를 백그라운드에서 실행
docker-compose -f $(dirname "$0")/../docker/docker-compose.yml up -d

echo "MySQL 컨테이너가 백그라운드에서 시작되었습니다."
