package br.com.lab.shortener.application.usecase;

import br.com.lab.shortener.application.exception.UrlNotFoundException;
import br.com.lab.shortener.application.port.out.UrlCachePort;
import br.com.lab.shortener.application.port.out.UrlMappingRepositoryPort;
import br.com.lab.shortener.domain.model.ShortCode;
import br.com.lab.shortener.domain.model.UrlMapping;

public final class ResolveShortUrlUseCase {
    private final UrlMappingRepositoryPort repository;
    private final UrlCachePort cache;

    public ResolveShortUrlUseCase(UrlMappingRepositoryPort repository, UrlCachePort cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public UrlMapping execute(String rawCode) {
        ShortCode code;
        try { code = new ShortCode(rawCode); }
        catch (IllegalArgumentException ex) { throw new UrlNotFoundException(); }
        return cache.get(code).orElseGet(() -> {
            UrlMapping mapping = repository.findByCode(code).orElseThrow(UrlNotFoundException::new);
            cache.put(mapping);
            return mapping;
        });
    }
}
