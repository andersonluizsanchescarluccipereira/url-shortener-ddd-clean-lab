package br.com.lab.shortener.adapter.in.web;

import br.com.lab.shortener.application.usecase.CreateShortUrlUseCase;
import br.com.lab.shortener.domain.model.UrlMapping;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class CreateUrlController {
    private final CreateShortUrlUseCase useCase;
    private final String publicBaseUrl;

    public CreateUrlController(CreateShortUrlUseCase useCase,
            @Value("${app.public-base-url}") String publicBaseUrl) {
        this.useCase = useCase;
        this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
    }

    @PostMapping
    public ResponseEntity<CreateUrlResponse> create(@Valid @RequestBody CreateUrlRequest request) {
        UrlMapping mapping = useCase.execute(request.url());
        String code = mapping.code().value();
        String temporary = publicBaseUrl + "/r/" + code;
        String permanent = publicBaseUrl + "/p/" + code;
        return ResponseEntity.created(URI.create(temporary))
            .body(new CreateUrlResponse(code, temporary, permanent,
                mapping.target().value(), mapping.createdAt()));
    }

    public record CreateUrlRequest(@NotBlank(message = "A URL é obrigatória") String url) { }
    public record CreateUrlResponse(String code, String shortUrl, String permanentUrl,
            String originalUrl, Instant createdAt) { }
}
