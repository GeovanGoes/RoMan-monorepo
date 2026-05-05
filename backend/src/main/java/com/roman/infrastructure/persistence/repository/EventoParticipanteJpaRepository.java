package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.EventoParticipanteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoParticipanteJpaRepository extends JpaRepository<EventoParticipanteJpaEntity, UUID> {
    Optional<EventoParticipanteJpaEntity> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
    List<EventoParticipanteJpaEntity> findByEventoId(UUID eventoId);
    boolean existsByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
}
