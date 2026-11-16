#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
command -v podman >/dev/null 2>&1 || { echo 'Podman não instalado.' >&2; exit 1; }
source scripts/_compose.sh
if [[ ! -f .env ]]; then cp .env.example .env; fi
echo '[1/4] Subindo Redis e Cassandra (Cassandra pode demorar na primeira execução)...'
"${COMPOSE[@]}" up -d redis cassandra
echo '[2/4] Aguardando CQL...'
ready=0
for _ in $(seq 1 120); do
  if podman exec shortener-cassandra cqlsh -e 'DESCRIBE CLUSTER' >/dev/null 2>&1; then
    ready=1; break
  fi
  sleep 5
done
if [[ "$ready" != "1" ]]; then
  echo 'Cassandra não ficou pronto; verifique: podman logs shortener-cassandra' >&2
  exit 1
fi
echo '[3/4] Aplicando schema do Cassandra...'
podman exec -i shortener-cassandra cqlsh < infra/cassandra/schema.cql
echo '[4/4] Build da imagem Java 21 e subida das APIs/Nginx...'
podman build -t localhost/url-shortener-lab:dev .
"${COMPOSE[@]}" up -d api1 api2 nginx
echo 'API: http://localhost:8080 | health: http://localhost:8080/actuator/health'
echo 'Se retornar 502 durante o boot, acompanhe: ./scripts/logs.sh'
