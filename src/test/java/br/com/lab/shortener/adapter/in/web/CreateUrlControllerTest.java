package br.com.lab.shortener.adapter.in.web;

import br.com.lab.shortener.application.usecase.CreateShortUrlUseCase;
import br.com.lab.shortener.domain.model.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreateUrlControllerTest {
    @Test void criaUrlComStatus201EContratoCompleto() throws Exception {
        CreateShortUrlUseCase useCase = mock(CreateShortUrlUseCase.class);
        when(useCase.execute(eq("https://example.com/a"))).thenReturn(new UrlMapping(
            new ShortCode("AbC12345"), TargetUrl.of("https://example.com/a"), Instant.EPOCH));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new CreateUrlController(useCase, "http://localhost:8080"))
            .setControllerAdvice(new ApiExceptionHandler()).build();
        mvc.perform(post("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://example.com/a\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost:8080/r/AbC12345"))
            .andExpect(jsonPath("$.code").value("AbC12345"))
            .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/r/AbC12345"))
            .andExpect(jsonPath("$.permanentUrl").value("http://localhost:8080/p/AbC12345"));
    }
}
