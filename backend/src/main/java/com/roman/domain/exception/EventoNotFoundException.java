package com.roman.domain.exception;

import java.util.UUID;

public class EventoNotFoundException extends DomainException {
    public EventoNotFoundException(UUID id) {
        super("Evento não encontrado: " + id);
    }
}
