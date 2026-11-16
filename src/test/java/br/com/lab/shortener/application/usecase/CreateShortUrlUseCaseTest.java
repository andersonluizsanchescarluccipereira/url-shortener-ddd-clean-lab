package br.com.lab.shortener.application.usecase;

import br.com.lab.shortener.application.exception.CodeAllocationException;
import br.com.lab.shortener.application.port.out.*;
import br.com.lab.shortener.domain.exception.InvalidTargetUrlException;
import br.com.lab.shortener.domain.model.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateShortUrlUseCaseTest {
    private final AtomicLong seq = new AtomicLong();
    private final IdSequencePort ids = seq::incrementAndGet;
    private final CodeEncoderPort encoder = id -> new ShortCode("ABCDEF" + id);
    private final UrlCachePort cache = new UrlCachePort() {
        public Optional<UrlMapping> get(ShortCode code) { return Optional.empty(); }
        public void put(UrlMapping mapping) { }
    };
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    @Test void salvaUrlEIncrementaId() {
        UrlMappingRepositoryPort repository = new UrlMappingRepositoryPort() {
            public boolean insertIfAbsent(UrlMapping mapping) { return true; }
            public Optional<UrlMapping> findByCode(ShortCode code) { return Optional.empty(); }
        };
        var useCase = new CreateShortUrlUseCase(ids, encoder, repository, cache, clock);
        assertEquals("ABCDEF1", useCase.execute("https://example.com").code().value());
        assertEquals("ABCDEF2", useCase.execute("https://example.com").code().value());
    }
    @Test void validaAntesDeConsumirId() {
        UrlMappingRepositoryPort repository = new UrlMappingRepositoryPort() {
            public boolean insertIfAbsent(UrlMapping mapping) { return true; }
            public Optional<UrlMapping> findByCode(ShortCode code) { return Optional.empty(); }
        };
        var useCase = new CreateShortUrlUseCase(ids, encoder, repository, cache, clock);
        assertThrows(InvalidTargetUrlException.class, () -> useCase.execute("file:///etc/passwd"));
        assertEquals(0, seq.get());
    }
    @Test void tentaNovosIdsEmCasoDeColisao() {
        UrlMappingRepositoryPort repository = new UrlMappingRepositoryPort() {
            public boolean insertIfAbsent(UrlMapping mapping) { return !mapping.code().value().equals("ABCDEF1"); }
            public Optional<UrlMapping> findByCode(ShortCode code) { return Optional.empty(); }
        };
        var useCase = new CreateShortUrlUseCase(ids, encoder, repository, cache, clock);
        assertEquals("ABCDEF2", useCase.execute("https://example.com").code().value());
    }
    @Test void esgotaTentativas() {
        UrlMappingRepositoryPort repository = new UrlMappingRepositoryPort() {
            public boolean insertIfAbsent(UrlMapping mapping) { return false; }
            public Optional<UrlMapping> findByCode(ShortCode code) { return Optional.empty(); }
        };
        var useCase = new CreateShortUrlUseCase(ids, encoder, repository, cache, clock);
        assertThrows(CodeAllocationException.class, () -> useCase.execute("https://example.com"));
        assertEquals(5, seq.get());
    }
}
