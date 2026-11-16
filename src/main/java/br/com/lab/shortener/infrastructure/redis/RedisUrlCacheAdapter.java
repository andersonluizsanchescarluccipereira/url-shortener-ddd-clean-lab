package br.com.lab.shortener.infrastructure.redis;

import br.com.lab.shortener.application.port.out.UrlCachePort;
import br.com.lab.shortener.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUrlCacheAdapter implements UrlCachePort {
    private static final Duration TTL = Duration.ofMinutes(30);
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public RedisUrlCacheAdapter(StringRedisTemplate redis, ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
    }
    private String key(ShortCode code) { return "shortener:cache:" + code.value(); }

    @Override public Optional<UrlMapping> get(ShortCode code) {
        try {
            String json = redis.opsForValue().get(key(code));
            if (json == null) return Optional.empty();
            CacheEntry cached = mapper.readValue(json, CacheEntry.class);
            return Optional.of(new UrlMapping(code, TargetUrl.of(cached.url()), cached.createdAt()));
        } catch (Exception ignored) {
            // Cache está fora do caminho crítico de leitura: tenta Cassandra.
            return Optional.empty();
        }
    }

    @Override public void put(UrlMapping mapping) {
        try {
            String json = mapper.writeValueAsString(new CacheEntry(mapping.target().value(), mapping.createdAt()));
            redis.opsForValue().set(key(mapping.code()), json, TTL);
        } catch (Exception ignored) {
            // Sem perda de dado: Cassandra foi gravado/consultado com sucesso.
        }
    }

    private record CacheEntry(String url, Instant createdAt) { }
}
