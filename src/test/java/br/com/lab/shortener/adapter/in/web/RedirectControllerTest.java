package br.com.lab.shortener.adapter.in.web;

import br.com.lab.shortener.application.usecase.ResolveShortUrlUseCase;
import br.com.lab.shortener.domain.model.*;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RedirectControllerTest {
    private MockMvc mvc;
    @BeforeEach void setup() {
        ResolveShortUrlUseCase resolver = mock(ResolveShortUrlUseCase.class);
        when(resolver.execute(eq("AbC12345"))).thenReturn(new UrlMapping(
            new ShortCode("AbC12345"), TargetUrl.of("https://example.com/path?q=1"), Instant.EPOCH));
        mvc = MockMvcBuilders.standaloneSetup(new RedirectController(resolver))
            .setControllerAdvice(new ApiExceptionHandler()).build();
    }
    @Test void temporarioRetorna302() throws Exception {
        mvc.perform(get("/r/AbC12345"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "https://example.com/path?q=1"))
            .andExpect(header().string("Cache-Control", "no-store"));
    }
    @Test void permanenteRetorna301() throws Exception {
        mvc.perform(get("/p/AbC12345"))
            .andExpect(status().isMovedPermanently())
            .andExpect(header().string("Location", "https://example.com/path?q=1"))
            .andExpect(header().string("Cache-Control", "public, max-age=86400"));
    }
}
