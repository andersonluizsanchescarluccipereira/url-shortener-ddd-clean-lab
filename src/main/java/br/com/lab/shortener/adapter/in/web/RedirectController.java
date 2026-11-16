package br.com.lab.shortener.adapter.in.web;

import br.com.lab.shortener.application.usecase.ResolveShortUrlUseCase;
import br.com.lab.shortener.domain.model.UrlMapping;
import java.net.URI;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RedirectController {
    private final ResolveShortUrlUseCase useCase;
    public RedirectController(ResolveShortUrlUseCase useCase) { this.useCase = useCase; }

    /** 302: o cliente deve consultar o encurtador novamente em outra visita. */
    @GetMapping("/r/{code}")
    public ResponseEntity<Void> temporary(@PathVariable String code) {
        return redirect(code, HttpStatus.FOUND, CacheControl.noStore().getHeaderValue());
    }

    /** 301: browsers e proxies podem memorizar o destino. O mapeamento é imutável. */
    @GetMapping("/p/{code}")
    public ResponseEntity<Void> permanent(@PathVariable String code) {
        return redirect(code, HttpStatus.MOVED_PERMANENTLY, "public, max-age=86400");
    }

    private ResponseEntity<Void> redirect(String code, HttpStatus status, String cacheControl) {
        UrlMapping mapping = useCase.execute(code);
        return ResponseEntity.status(status)
            .header(HttpHeaders.LOCATION, URI.create(mapping.target().value()).toASCIIString())
            .header(HttpHeaders.CACHE_CONTROL, cacheControl)
            .build();
    }
}
