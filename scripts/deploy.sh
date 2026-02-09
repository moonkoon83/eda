#!/bin/bash
set -e

# --- 파라미터 검증 ---
if [ -z "$1" ]; then
  echo "오류: 배포할 환경을 입력해주세요."
  echo "사용법: $0 <environment>"
  echo "예시: $0 develop"
  exit 1
fi
ENV=$1
KUSTOMIZE_DIR=$(dirname "$0")/../k8s/${ENV}

# --- Kustomize 디렉토리 확인 ---
if [ ! -d "${KUSTOMIZE_DIR}" ]; then
  echo "오류: '${KUSTOMIZE_DIR}'를 찾을 수 없습니다."
  echo "유효한 환경 이름(develop, master 등)을 입력했는지 확인해주세요."
  exit 1
fi

echo "✅ '${ENV}' 환경에 애플리케이션을 배포합니다..."

# -k 옵션을 사용하여 Kustomize로 리소스를 배포합니다.
# Kustomize가 ConfigMap의 변경을 감지하고 자동으로 롤링 업데이트를 트리거합니다.
kubectl apply -k "${KUSTOMIZE_DIR}"

echo -e "\n🚀 배포가 완료되었습니다. 잠시 후 다음 명령어로 상태를 확인하세요:"
echo "   kubectl get all -n eda-${ENV}"
