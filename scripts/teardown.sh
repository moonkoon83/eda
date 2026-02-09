#!/bin/bash
set -e

# --- 파라미터 검증 ---
if [ -z "$1" ]; then
  echo "오류: 삭제할 환경을 입력해주세요."
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

echo "🗑️  '${ENV}' 환경의 모든 리소스를 삭제합니다..."

# -k 옵션을 사용하여 Kustomize로 리소스를 삭제합니다.
kubectl delete -k "${KUSTOMIZE_DIR}"

echo "✅ '${ENV}' 환경의 리소스 삭제가 완료되었습니다."