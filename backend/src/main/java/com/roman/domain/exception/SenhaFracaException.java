package com.roman.domain.exception;

public class SenhaFracaException extends DomainException {
    public SenhaFracaException() { super("A senha deve ter pelo menos 8 caracteres e ao menos um dígito."); }
}
