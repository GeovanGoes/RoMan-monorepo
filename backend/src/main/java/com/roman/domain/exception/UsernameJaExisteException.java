package com.roman.domain.exception;

public class UsernameJaExisteException extends DomainException {
    public UsernameJaExisteException(String username) {
        super("Username já existe: " + username);
    }
}
