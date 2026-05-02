package com.roman.domain.exception;

import java.util.UUID;

public class ParticipanteJaVinculadoException extends DomainException {
    public ParticipanteJaVinculadoException(UUID participanteId, UUID eventoId) {
        super("Participante " + participanteId + " já está vinculado ao evento " + eventoId);
    }
}
