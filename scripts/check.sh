#!/usr/bin/env bash
set -euo pipefail
BASE="${BASE:-http://localhost:8080}"
echo '== Health =='
curl -is "$BASE/actuator/health" | sed -n '1,18p'
echo; echo '== Criar URL =='
response="$(curl -fsS -X POST "$BASE/api/v1/urls" -H 'Content-Type: application/json' \
  -d '{"url":"https://www.example.com/artigos/clean-architecture?tag=java"}')"
echo "$response"
if command -v python3 >/dev/null; then
  code="$(printf '%s' "$response" | python3 -c 'import json,sys; print(json.load(sys.stdin)["code"])')"
  echo; echo "== 302 /r/$code =="
  curl -sI "$BASE/r/$code" | sed -n '1,15p'
  echo; echo "== 301 /p/$code =="
  curl -sI "$BASE/p/$code" | sed -n '1,15p'
fi
echo; echo '== 400 URL inválida =='
curl -si -X POST "$BASE/api/v1/urls" -H 'Content-Type: application/json' \
  -d '{"url":"javascript:alert(1)"}' | sed -n '1,20p'
