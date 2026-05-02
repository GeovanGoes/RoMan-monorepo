package com.roman.domain.repository;

import com.roman.domain.entity.EventoParticipante;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoParticipanteRepository {
    EventoParticipante save(EventoParticipante eventoParticipante);
    Optional<EventoParticipante> findByEventoIdAndParticipanteId(UUID eventoId, UUID participanteId);
    List<EventoParticipante> findByEventoId(UUID eventoId);
    boolean existsByEventoIdAndParticipanteId(UUID eventoId, UUID participanteId);
    void delete(EventoParticipante eventoParticipante);
}
