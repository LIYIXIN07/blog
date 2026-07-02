#!/usr/bin/env bash
set -euo pipefail

REMOTE_HOME="${REMOTE_HOME:-/home}"
JAR_NAME="${JAR_NAME:-blog-backend-1.0.0.jar}"
JAR_PATH="${REMOTE_HOME}/${JAR_NAME}"
LOG_FILE="${REMOTE_HOME}/blog-backend.log"
SPRING_PROFILE="${SPRING_PROFILE:-prod}"

cd "${REMOTE_HOME}"

latest_jar="$(ls -t blog-backend-*.jar 2>/dev/null | head -1 || true)"
if [ -n "${latest_jar}" ] && [ "${latest_jar}" != "${JAR_NAME}" ]; then
  mv -f "${latest_jar}" "${JAR_PATH}"
fi

if [ ! -f "${JAR_PATH}" ]; then
  echo "未找到后端 JAR：${JAR_PATH}"
  exit 1
fi

pid="$(pgrep -f "${JAR_PATH}" || true)"
if [ -n "${pid}" ]; then
  kill "${pid}" || true
  sleep 3
fi

nohup java -jar "${JAR_PATH}" --spring.profiles.active="${SPRING_PROFILE}" > "${LOG_FILE}" 2>&1 &
echo "后端已重启：${JAR_PATH}"
