package com.roman.domain.exception;

public class EmailJaExisteException extends DomainException {
    public EmailJaExisteException() { super("E-mail já cadastrado."); }
}
