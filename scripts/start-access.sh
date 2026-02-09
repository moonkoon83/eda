#!/bin/bash
set -e

# --- 파라미터 검증 ---
if [ -z "$1" ]; then
  echo "오류: 포트 포워딩할 환경을 입력해주세요."
  echo "사용법: $0 <environment>"
  echo "예시: $0 local"
  exit 1
fi
ENV=$1
NAMESPACE="eda-${ENV}"
PID_FILE=".port-forward-${ENV}.pid"

if [ -f "${PID_FILE}" ]; then
  echo "오류: 이미 '${ENV}' 환경에 대한 포트 포워딩이 실행 중입니다. (PID: $(cat ${PID_FILE}))"
  echo "중지하려면 './scripts/stop-access.sh ${ENV}'를 실행하세요."
  exit 1
fi

echo "🔌 '${NAMESPACE}'에 대한 포트 포워딩을 백그라운드에서 시작합니다..."

# kubectl port-forward를 백그라운드에서 실행
kubectl port-forward service/eda-app-service 8080:8080 -n "${NAMESPACE}" &

# 백그라운드 프로세스의 PID를 저장
PID=$!
echo ${PID} > "${PID_FILE}"

echo "✅ 포트 포워딩이 PID ${PID}로 시작되었습니다."
echo "   이제 http://localhost:8080 으로 접속할 수 있습니다."
echo "   중지하려면 './scripts/stop-access.sh ${ENV}'를 실행하세요."