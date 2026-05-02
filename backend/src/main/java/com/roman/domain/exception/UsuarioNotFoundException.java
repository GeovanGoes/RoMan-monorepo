package com.roman.domain.exception;

import java.util.UUID;

public class UsuarioNotFoundException extends DomainException {
    public UsuarioNotFoundException(UUID id) { super("Usuário não encontrado: " + id); }
    public UsuarioNotFoundException(String msg) { super(msg); }
}
