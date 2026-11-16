#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
source scripts/_compose.sh
"${COMPOSE[@]}" logs -f "${@:-api1}" 
