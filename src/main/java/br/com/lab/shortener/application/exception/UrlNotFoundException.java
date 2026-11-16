package br.com.lab.shortener.application.exception;
public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException() { super("Código de URL não encontrado."); }
}
