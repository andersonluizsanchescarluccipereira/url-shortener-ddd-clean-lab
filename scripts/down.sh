#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
source scripts/_compose.sh
"${COMPOSE[@]}" down
echo 'Dados persistidos em volumes. Para apagar TUDO: podman compose down -v'
