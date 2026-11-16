-- DEV: somente 1 nó. Produção requer RF >= 3 e consistência adequada ao SLA.
CREATE KEYSPACE IF NOT EXISTS shortener WITH replication = {
  'class': 'SimpleStrategy', 'replication_factor': 1
} AND durable_writes = true;

USE shortener;

CREATE TABLE IF NOT EXISTS url_mappings (
  code text PRIMARY KEY,
  long_url text,
  created_at timestamp
);
-- Apenas consulta por PK. Não executamos ALLOW FILTERING.
-- INSERT ... IF NOT EXISTS impede que códigos repetidos sobrescrevam outros links.
