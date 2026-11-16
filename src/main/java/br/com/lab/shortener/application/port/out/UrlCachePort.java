package br.com.lab.shortener.application.port.out;
import br.com.lab.shortener.domain.model.ShortCode;
import br.com.lab.shortener.domain.model.UrlMapping;
import java.util.Optional;
public interface UrlCachePort {
    Optional<UrlMapping> get(ShortCode code);
    void put(UrlMapping mapping);
}
