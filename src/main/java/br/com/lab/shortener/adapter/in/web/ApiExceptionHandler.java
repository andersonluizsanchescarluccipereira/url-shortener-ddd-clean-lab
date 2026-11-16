package br.com.lab.shortener.adapter.in.web;

import br.com.lab.shortener.application.exception.CodeAllocationException;
import br.com.lab.shortener.application.exception.UrlNotFoundException;
import br.com.lab.shortener.domain.exception.InvalidTargetUrlException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidTargetUrlException.class)
    public ResponseEntity<ProblemDetail> invalidUrl(InvalidTargetUrlException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> invalidRequest(MethodArgumentNotValidException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Campo 'url' obrigatório e não pode ser vazio.");
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> invalidJson(HttpMessageNotReadableException ex) {
        return problem(HttpStatus.BAD_REQUEST, "JSON da requisição inválido.");
    }
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(UrlNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(CodeAllocationException.class)
    public ResponseEntity<ProblemDetail> allocationUnavailable(CodeAllocationException ex) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> internal(Exception ex, HttpServletRequest request) {
        // Não expor stacktrace nem informações internas dos bancos em respostas HTTP.
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "O serviço está temporariamente indisponível.");
    }
    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String detail) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setTitle(status.getReasonPhrase());
        return ResponseEntity.status(status).body(p);
    }
}
