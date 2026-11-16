package br.com.lab.shortener.infrastructure.config;

import br.com.lab.shortener.application.port.out.*;
import br.com.lab.shortener.application.usecase.*;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {
    @Bean Clock clock() { return Clock.systemUTC(); }
    @Bean CreateShortUrlUseCase createShortUrlUseCase(IdSequencePort sequence, CodeEncoderPort encoder,
            UrlMappingRepositoryPort repo, UrlCachePort cache, Clock clock) {
        return new CreateShortUrlUseCase(sequence, encoder, repo, cache, clock);
    }
    @Bean ResolveShortUrlUseCase resolveShortUrlUseCase(UrlMappingRepositoryPort repo, UrlCachePort cache) {
        return new ResolveShortUrlUseCase(repo, cache);
    }
}
