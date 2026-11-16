package br.com.lab.shortener.infrastructure.encoding;

import br.com.lab.shortener.application.port.out.CodeEncoderPort;
import br.com.lab.shortener.domain.model.ShortCode;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashidsCodeEncoderAdapter implements CodeEncoderPort {
    private final Hashids hashids;
    public HashidsCodeEncoderAdapter(@Value("${app.hashids.salt}") String salt) {
        if (salt == null || salt.length() < 16) throw new IllegalArgumentException("HASHIDS_SALT precisa ter pelo menos 16 caracteres.");
        this.hashids = new Hashids(salt, 8);
    }
    @Override public ShortCode encode(long id) {
        if (id <= 0) throw new IllegalArgumentException("ID deve ser positivo.");
        return new ShortCode(hashids.encode(id));
    }
}
