package br.com.lab.shortener.application.exception;
public class CodeAllocationException extends RuntimeException {
    public CodeAllocationException() { super("Não foi possível reservar um código exclusivo. Tente novamente."); }
}
