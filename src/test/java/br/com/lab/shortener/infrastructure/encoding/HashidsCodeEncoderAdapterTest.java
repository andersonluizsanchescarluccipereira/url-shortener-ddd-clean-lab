package br.com.lab.shortener.infrastructure.encoding;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashidsCodeEncoderAdapterTest {
    @Test void codigoAlfanumericoMinimoOitoSemSequenciaExposta() {
        var adapter = new HashidsCodeEncoderAdapter("salt-do-laboratorio-12345");
        String a = adapter.encode(1).value();
        String b = adapter.encode(2).value();
        assertTrue(a.matches("[A-Za-z0-9]{8,}"));
        assertTrue(b.matches("[A-Za-z0-9]{8,}"));
        assertNotEquals(a, b);
        assertEquals(a, adapter.encode(1).value());
    }
    @Test void rejeitaSaltFraco() {
        assertThrows(IllegalArgumentException.class, () -> new HashidsCodeEncoderAdapter("short"));
    }
}
