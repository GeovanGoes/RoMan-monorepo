package com.roman.domain.repository;

import com.roman.domain.entity.EventoParticipante;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoParticipanteRepository {
    EventoParticipante save(EventoParticipante eventoParticipante);
    Optional<EventoParticipante> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
    List<EventoParticipante> findByEventoId(UUID eventoId);
    boolean existsByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
    void delete(EventoParticipante eventoParticipante);
}
