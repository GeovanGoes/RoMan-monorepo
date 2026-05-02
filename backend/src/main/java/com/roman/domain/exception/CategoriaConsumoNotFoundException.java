package com.roman.domain.exception;

import java.util.UUID;

public class CategoriaConsumoNotFoundException extends DomainException {
    public CategoriaConsumoNotFoundException(UUID id) {
        super("Categoria de consumo não encontrada: " + id);
    }
}
