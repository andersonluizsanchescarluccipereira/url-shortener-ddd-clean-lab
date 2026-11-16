package br.com.lab.shortener.domain.model;

import java.time.Instant;
import java.util.Objects;

/** Aggregate Root: código imutável associado ao destino original. */
public record UrlMapping(ShortCode code, TargetUrl target, Instant createdAt) {
    public UrlMapping {
        Objects.requireNonNull(code);
        Objects.requireNonNull(target);
        Objects.requireNonNull(createdAt);
    }
}
