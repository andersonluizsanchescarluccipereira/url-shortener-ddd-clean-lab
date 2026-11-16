#!/usr/bin/env bash
set -euo pipefail
if podman compose version >/dev/null 2>&1; then
  COMPOSE=(podman compose)
elif command -v podman-compose >/dev/null 2>&1; then
  COMPOSE=(podman-compose)
else
  echo 'Instale podman-compose (ou um provider para podman compose).' >&2
  exit 1
fi
