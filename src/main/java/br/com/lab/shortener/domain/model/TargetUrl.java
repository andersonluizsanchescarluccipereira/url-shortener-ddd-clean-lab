package br.com.lab.shortener.domain.model;

import br.com.lab.shortener.domain.exception.InvalidTargetUrlException;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Value Object: valida uma URL HTTP(S) externa; nunca acessa a rede. */
public final class TargetUrl {
    private final String value;
    private TargetUrl(String value) { this.value = Objects.requireNonNull(value); }
    public String value() { return value; }
    @Override public boolean equals(Object other) {
        return other instanceof TargetUrl t && value.equals(t.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
    private static final int MAX_BYTES = 2048;
    private static final Pattern LABEL = Pattern.compile("[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?");
    private static final Pattern IPV4_LIKE = Pattern.compile("[0-9.]+");

    public static TargetUrl of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidTargetUrlException("A URL é obrigatória.");
        }
        String input = raw.trim();
        if (input.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            throw new InvalidTargetUrlException("A URL não pode ultrapassar 2048 bytes.");
        }
        if (input.codePoints().anyMatch(c -> Character.isWhitespace(c) || Character.isISOControl(c))) {
            throw new InvalidTargetUrlException("A URL não pode conter espaços nem caracteres de controle.");
        }
        try {
            URI uri = new URI(input).parseServerAuthority();
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new InvalidTargetUrlException("Somente esquemas http e https são aceitos.");
            }
            if (uri.getRawUserInfo() != null) {
                throw new InvalidTargetUrlException("URLs com usuário/senha não são permitidas.");
            }
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new InvalidTargetUrlException("Hostname inválido ou ausente (use punycode para IDN). ");
            }
            String host = IDN.toASCII(uri.getHost(), IDN.USE_STD3_ASCII_RULES).toLowerCase(Locale.ROOT);
            if (host.length() > 253 || !host.contains(".") || host.endsWith(".") || host.contains(":")
                || IPV4_LIKE.matcher(host).matches()) {
                throw new InvalidTargetUrlException("Utilize um domínio público válido (IP literal e localhost não são aceitos). ");
            }
            String[] labels = host.split("\\.", -1);
            for (String label : labels) {
                if (!LABEL.matcher(label).matches()) {
                    throw new InvalidTargetUrlException("O domínio contém caracteres inválidos.");
                }
            }
            String tld = labels[labels.length - 1];
            if (tld.length() < 2 || tld.matches("[0-9]+")) {
                throw new InvalidTargetUrlException("O domínio precisa de um TLD válido.");
            }
            if (host.equals("localhost") || host.endsWith(".localhost") || host.endsWith(".local")
                || host.endsWith(".internal") || host.endsWith(".invalid") || host.endsWith(".test")) {
                throw new InvalidTargetUrlException("Hosts locais ou reservados não são aceitos.");
            }
            if (uri.getPort() == 0 || uri.getPort() > 65535) {
                throw new InvalidTargetUrlException("Porta fora do intervalo permitido.");
            }
            // Preserva path, query, fragment e a grafia de entrada; a validação não é uma requisição HTTP.
            return new TargetUrl(input);
        } catch (URISyntaxException | IllegalArgumentException ex) {
            if (ex instanceof InvalidTargetUrlException) throw (InvalidTargetUrlException) ex;
            throw new InvalidTargetUrlException("A URL enviada não possui sintaxe válida.");
        }
    }
}
