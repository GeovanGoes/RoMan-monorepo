package com.roman.interfaces.dto.response;

import com.roman.domain.entity.Participante;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParticipanteResponse(
        UUID id,
        String nome,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ParticipanteResponse from(Participante p) {
        return new ParticipanteResponse(p.getId(), p.getNome(), p.getUsername(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
