#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ -f run-local.env ]]; then
  set -a
  # shellcheck source=/dev/null
  source run-local.env
  set +a
fi
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"
exec ./mvnw spring-boot:run "$@"
