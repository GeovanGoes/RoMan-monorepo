package com.roman.domain.exception;

import java.util.UUID;

public class EventoParticipanteNotFoundException extends DomainException {
    public EventoParticipanteNotFoundException(UUID participanteId, UUID eventoId) {
        super("Participante " + participanteId + " não está vinculado ao evento " + eventoId);
    }
}
