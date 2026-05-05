package com.roman.interfaces.dto.response;

import com.roman.domain.entity.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParticipanteResponse(
        UUID id,
        String nome,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ParticipanteResponse from(Usuario u) {
        return new ParticipanteResponse(u.getId(), u.getNome(), u.getUsername(),
                u.getCreatedAt(), u.getUpdatedAt());
    }
}
