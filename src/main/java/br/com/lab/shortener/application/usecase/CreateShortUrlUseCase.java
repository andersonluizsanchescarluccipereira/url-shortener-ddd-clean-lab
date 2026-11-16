package br.com.lab.shortener.application.usecase;

import br.com.lab.shortener.application.exception.CodeAllocationException;
import br.com.lab.shortener.application.port.out.CodeEncoderPort;
import br.com.lab.shortener.application.port.out.IdSequencePort;
import br.com.lab.shortener.application.port.out.UrlCachePort;
import br.com.lab.shortener.application.port.out.UrlMappingRepositoryPort;
import br.com.lab.shortener.domain.model.TargetUrl;
import br.com.lab.shortener.domain.model.UrlMapping;
import java.time.Clock;
import java.time.Instant;

public final class CreateShortUrlUseCase {
    private static final int MAX_COLLISION_RETRIES = 5;
    private final IdSequencePort sequence;
    private final CodeEncoderPort encoder;
    private final UrlMappingRepositoryPort repository;
    private final UrlCachePort cache;
    private final Clock clock;

    public CreateShortUrlUseCase(IdSequencePort sequence, CodeEncoderPort encoder,
            UrlMappingRepositoryPort repository, UrlCachePort cache, Clock clock) {
        this.sequence = sequence;
        this.encoder = encoder;
        this.repository = repository;
        this.cache = cache;
        this.clock = clock;
    }

    public UrlMapping execute(String rawUrl) {
        // Validação vem antes da geração do ID e da escrita no banco.
        TargetUrl target = TargetUrl.of(rawUrl);
        for (int attempt = 0; attempt < MAX_COLLISION_RETRIES; attempt++) {
            long id = sequence.nextId();
            UrlMapping mapping = new UrlMapping(encoder.encode(id), target, Instant.now(clock));
            if (repository.insertIfAbsent(mapping)) {
                cache.put(mapping); // cache best-effort: a fonte da verdade é o Cassandra
                return mapping;
            }
        }
        throw new CodeAllocationException();
    }
}
