package com.roman.domain.exception;

import java.util.UUID;

public class ParticipanteNotFoundException extends DomainException {
    public ParticipanteNotFoundException(UUID id) {
        super("Participante não encontrado: " + id);
    }
}
