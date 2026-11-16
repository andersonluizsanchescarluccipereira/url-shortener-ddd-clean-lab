package br.com.lab.shortener.application.port.out;
import br.com.lab.shortener.domain.model.ShortCode;
public interface CodeEncoderPort { ShortCode encode(long id); }
