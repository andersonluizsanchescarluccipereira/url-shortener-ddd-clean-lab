package br.com.lab.shortener.application.port.out;
import br.com.lab.shortener.domain.model.ShortCode;
import br.com.lab.shortener.domain.model.UrlMapping;
import java.util.Optional;
public interface UrlMappingRepositoryPort {
    /** Compare-and-set atômico: não sobrescreve códigos existentes. */
    boolean insertIfAbsent(UrlMapping mapping);
    Optional<UrlMapping> findByCode(ShortCode code);
}
