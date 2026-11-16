package br.com.lab.shortener.domain.exception;

public class InvalidTargetUrlException extends IllegalArgumentException {
    public InvalidTargetUrlException(String message) { super(message); }
}
