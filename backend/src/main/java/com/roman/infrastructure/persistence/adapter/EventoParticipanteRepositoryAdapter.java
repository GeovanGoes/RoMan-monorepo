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
        EventoParticipanteJpaEntity entity = toEntity(ep);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<EventoParticipante> findByEventoIdAndParticipanteId(UUID eventoId, UUID participanteId) {
        return jpaRepository.findByEventoIdAndParticipanteId(eventoId, participanteId).map(this::toDomain);
    }

    @Override
    public List<EventoParticipante> findByEventoId(UUID eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByEventoIdAndParticipanteId(UUID eventoId, UUID participanteId) {
        return jpaRepository.existsByEventoIdAndParticipanteId(eventoId, participanteId);
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
                .participanteId(ep.getParticipanteId())
                .menorDeIdade(ep.isMenorDeIdade())
                .categoriasExcluidas(categorias)
                .createdAt(ep.getCreatedAt())
                .build();
    }

    private EventoParticipante toDomain(EventoParticipanteJpaEntity e) {
        Set<UUID> categoriasIds = e.getCategoriasExcluidas().stream()
                .map(CategoriaConsumoJpaEntity::getId)
                .collect(Collectors.toSet());

        return new EventoParticipante(e.getId(), e.getEventoId(), e.getParticipanteId(),
                e.isMenorDeIdade(), categoriasIds, e.getCreatedAt());
    }
}
