package br.com.lab.shortener.infrastructure.cassandra;

import br.com.lab.shortener.application.port.out.UrlMappingRepositoryPort;
import br.com.lab.shortener.domain.model.*;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CassandraUrlMappingRepositoryAdapter implements UrlMappingRepositoryPort {
    private final CqlSession session;
    private final PreparedStatement insert;
    private final PreparedStatement select;

    public CassandraUrlMappingRepositoryAdapter(CqlSession session) {
        this.session = session;
        this.insert = session.prepare("INSERT INTO url_mappings (code, long_url, created_at) "
            + "VALUES (?, ?, ?) IF NOT EXISTS");
        this.select = session.prepare("SELECT code, long_url, created_at FROM url_mappings WHERE code = ?");
    }

    @Override public boolean insertIfAbsent(UrlMapping mapping) {
        BoundStatement bound = insert.bind(mapping.code().value(), mapping.target().value(), mapping.createdAt());
        Row result = session.execute(bound).one();
        return result != null && result.getBoolean("[applied]");
    }

    @Override public Optional<UrlMapping> findByCode(ShortCode code) {
        Row row = session.execute(select.bind(code.value())).one();
        if (row == null) return Optional.empty();
        String url = row.getString("long_url");
        Instant createdAt = row.getInstant("created_at");
        if (url == null || createdAt == null) throw new IllegalStateException("Registro Cassandra incompleto.");
        return Optional.of(new UrlMapping(code, TargetUrl.of(url), createdAt));
    }
}
