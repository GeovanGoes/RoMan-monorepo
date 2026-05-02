package com.roman.domain.exception;

public class TokenInvalidoException extends DomainException {
    public TokenInvalidoException() { super("Token inválido ou expirado."); }
}
