package br.com.lab.shortener.domain.model;

import br.com.lab.shortener.domain.exception.InvalidTargetUrlException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class TargetUrlTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "https://example.com", "http://www.example.com/path?q=java",
        "https://sub.example.com:8443/a#frag", "https://xn--bcher-kva.de/seite"
    })
    void aceitaUrlsPublicasHttpHttps(String url) {
        assertEquals(url, TargetUrl.of(url).value());
    }
    @ParameterizedTest
    @ValueSource(strings = {
        "", "ftp://example.com/a", "javascript:alert(1)", "https://localhost/a",
        "http://127.0.0.1/a", "https://192.168.1.30", "https://example.local",
        "https://example.com/a b", "https://admin:123@example.com/a", "site.com/a",
        "https://bad_domain.com", "https://example.com:99999/a", "https://example.test"
    })
    void rejeitaUrlsInvalidas(String url) {
        assertThrows(InvalidTargetUrlException.class, () -> TargetUrl.of(url));
    }
    @Test void rejeitaNull() { assertThrows(InvalidTargetUrlException.class, () -> TargetUrl.of(null)); }
    @Test void limitaBytes() {
        assertThrows(InvalidTargetUrlException.class, () -> TargetUrl.of("https://example.com/" + "a".repeat(2048)));
    }
}
