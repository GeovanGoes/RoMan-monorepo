package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.infrastructure.persistence.entity.CategoriaConsumoJpaEntity;
import com.roman.infrastructure.persistence.entity.EventoParticipanteJpaEntity;
import com.roman.infrastructure.persistence.repository.CategoriaConsumoJpaRepository;
import com.roman.infrastructure.persistence.repository.EventoParticipanteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EventoParticipanteRepositoryAdapter implements EventoParticipanteRepository {

    private final EventoParticipanteJpaRepository jpaRepository;
    private final CategoriaConsumoJpaRepository categoriaJpaRepository;

    public EventoParticipanteRepositoryAdapter(EventoParticipanteJpaRepository jpaRepository,
                                               CategoriaConsumoJpaRepository categoriaJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.categoriaJpaRepository = categoriaJpaRepository;
    }

    @Override
    public EventoParticipante save(EventoParticipante ep) {
        return toDomain(jpaRepository.save(toEntity(ep)));
    }

    @Override
    public Optional<EventoParticipante> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId) {
        return jpaRepository.findByEventoIdAndUsuarioId(eventoId, usuarioId).map(this::toDomain);
    }

    @Override
    public List<EventoParticipante> findByEventoId(UUID eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId) {
        return jpaRepository.existsByEventoIdAndUsuarioId(eventoId, usuarioId);
    }

    @Override
    public void delete(EventoParticipante ep) {
        jpaRepository.deleteById(ep.getId());
    }

    private EventoParticipanteJpaEntity toEntity(EventoParticipante ep) {
        Set<CategoriaConsumoJpaEntity> categorias = ep.getCategoriasExcluidas().stream()
                .map(id -> categoriaJpaRepository.getReferenceById(id))
                .collect(Collectors.toSet());

        return EventoParticipanteJpaEntity.builder()
                .id(ep.getId())
                .eventoId(ep.getEventoId())
                .usuarioId(ep.getUsuarioId())
                .menorDeIdade(ep.isMenorDeIdade())
                .categoriasExcluidas(categorias)
                .createdAt(ep.getCreatedAt())
                .build();
    }

    private EventoParticipante toDomain(EventoParticipanteJpaEntity e) {
        Set<UUID> categoriasIds = e.getCategoriasExcluidas().stream()
                .map(CategoriaConsumoJpaEntity::getId)
                .collect(Collectors.toSet());

        return new EventoParticipante(e.getId(), e.getEventoId(), e.getUsuarioId(),
                e.isMenorDeIdade(), categoriasIds, e.getCreatedAt());
    }
}
