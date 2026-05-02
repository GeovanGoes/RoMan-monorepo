package com.roman.domain.exception;

public class UsernameJaExisteParaUsuarioException extends DomainException {
    public UsernameJaExisteParaUsuarioException(String username) {
        super("Username já existe: " + username);
    }
}
