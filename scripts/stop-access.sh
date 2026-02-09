#!/bin/bash
set -e

# --- 파라미터 검증 ---
if [ -z "$1" ]; then
  echo "오류: 중지할 포트 포워딩 환경을 입력해주세요."
  echo "사용법: $0 <environment>"
  echo "예시: $0 local"
  exit 1
fi
ENV=$1
PID_FILE=".port-forward-${ENV}.pid"

if [ ! -f "${PID_FILE}" ]; then
  echo "오류: '${ENV}' 환경에 대해 실행 중인 포트 포워딩이 없습니다."
  exit 1
fi

PID=$(cat ${PID_FILE})
echo "⏹️ PID ${PID}로 실행 중인 포트 포워딩을 중지합니다..."

# kill 명령어는 프로세스가 없을 때 오류를 반환할 수 있으므로, || true를 추가하여 스크립트가 중단되지 않게 함
kill ${PID} || true

# PID 파일 삭제
rm "${PID_FILE}"

echo "✅ 포트 포워딩이 중지되었습니다."
