package com.roman.interfaces.dto.response;

import com.roman.domain.entity.EventoParticipante;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record EventoParticipanteResponse(
        UUID id,
        UUID eventoId,
        UUID usuarioId,
        boolean menorDeIdade,
        Set<UUID> categoriasExcluidas,
        LocalDateTime createdAt
) {
    public static EventoParticipanteResponse from(EventoParticipante ep) {
        return new EventoParticipanteResponse(ep.getId(), ep.getEventoId(), ep.getUsuarioId(),
                ep.isMenorDeIdade(), ep.getCategoriasExcluidas(), ep.getCreatedAt());
    }
}
