package com.roman.interfaces.dto.response;

import com.roman.domain.entity.Evento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventoResponse(
        UUID id,
        String nome,
        String local,
        LocalDate dataInicio,
        LocalDate dataFim,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EventoResponse from(Evento e) {
        return new EventoResponse(e.getId(), e.getNome(), e.getLocal(),
                e.getDataInicio(), e.getDataFim(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
