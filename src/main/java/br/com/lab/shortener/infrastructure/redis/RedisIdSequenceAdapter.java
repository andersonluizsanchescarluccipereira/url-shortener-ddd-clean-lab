package br.com.lab.shortener.infrastructure.redis;

import br.com.lab.shortener.application.port.out.IdSequencePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisIdSequenceAdapter implements IdSequencePort {
    public static final String KEY = "shortener:id:sequence";
    private final StringRedisTemplate redis;
    public RedisIdSequenceAdapter(StringRedisTemplate redis) { this.redis = redis; }

    @Override public long nextId() {
        Long id = redis.opsForValue().increment(KEY);
        if (id == null || id <= 0) throw new IllegalStateException("Contador Redis indisponível/estourado.");
        return id;
    }
}
