package com.roman.domain.exception;

import java.util.UUID;

public class CompraNotFoundException extends DomainException {
    public CompraNotFoundException(UUID id) {
        super("Compra não encontrada: " + id);
    }
}
