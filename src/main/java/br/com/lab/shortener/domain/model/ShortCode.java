package br.com.lab.shortener.domain.model;

public record ShortCode(String value) {
    public ShortCode {
        if (value == null || !value.matches("[a-zA-Z0-9]{6,32}")) {
            throw new IllegalArgumentException("Código curto inválido.");
        }
    }
}
