#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if command -v mvn >/dev/null 2>&1; then
  mvn test
else
  podman run --rm -v "$(pwd):/workspace:Z" -w /workspace \
    docker.io/library/maven:3.9.11-eclipse-temurin-21 mvn test
fi
