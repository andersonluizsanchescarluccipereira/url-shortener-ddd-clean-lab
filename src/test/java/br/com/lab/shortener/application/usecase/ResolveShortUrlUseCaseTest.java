package br.com.lab.shortener.application.usecase;

import br.com.lab.shortener.application.exception.UrlNotFoundException;
import br.com.lab.shortener.application.port.out.*;
import br.com.lab.shortener.domain.model.*;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResolveShortUrlUseCaseTest {
    @Test void cacheHitNaoConsultaCassandra() {
        var expected = new UrlMapping(new ShortCode("AbC12345"), TargetUrl.of("https://example.com"), Instant.EPOCH);
        var queryCount = new AtomicInteger();
        UrlMappingRepositoryPort db = new UrlMappingRepositoryPort() {
            public boolean insertIfAbsent(UrlMapping m) { return true; }
            public Optional<UrlMapping> findByCode(ShortCode c) { queryCount.incrementAndGet(); return Optional.empty(); }
        };
        UrlCachePort cache = new UrlCachePort() {
            public Optional<UrlMapping> get(ShortCode c) { return Optional.of(expected); }
            public void put(UrlMapping m) { }
        };
        var uc = new ResolveShortUrlUseCase(db, cache);
        assertEquals(expected, uc.execute("AbC12345"));
        assertEquals(0, queryCount.get());
    }
    @Test void inexistenteRetorna404NoAdapterHttp() {
        UrlMappingRepositoryPort db = new UrlMappingRepositoryPort() {
            public boolean insertIfAbsent(UrlMapping m) { return true; }
            public Optional<UrlMapping> findByCode(ShortCode c) { return Optional.empty(); }
        };
        UrlCachePort cache = new UrlCachePort() {
            public Optional<UrlMapping> get(ShortCode c) { return Optional.empty(); }
            public void put(UrlMapping m) { }
        };
        var uc = new ResolveShortUrlUseCase(db, cache);
        assertThrows(UrlNotFoundException.class, () -> uc.execute("AbC12345"));
    }
}
