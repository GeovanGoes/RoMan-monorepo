package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.Evento;
import com.roman.domain.repository.EventoRepository;
import com.roman.infrastructure.persistence.entity.EventoJpaEntity;
import com.roman.infrastructure.persistence.repository.EventoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventoRepositoryAdapter implements EventoRepository {

    private final EventoJpaRepository jpaRepository;

    public EventoRepositoryAdapter(EventoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Evento save(Evento evento) {
        return toDomain(jpaRepository.save(toEntity(evento)));
    }

    @Override
    public Optional<Evento> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Evento> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    private EventoJpaEntity toEntity(Evento e) {
        return EventoJpaEntity.builder()
                .id(e.getId())
                .nome(e.getNome())
                .local(e.getLocal())
                .dataInicio(e.getDataInicio())
                .dataFim(e.getDataFim())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private Evento toDomain(EventoJpaEntity e) {
        return new Evento(e.getId(), e.getNome(), e.getLocal(),
                e.getDataInicio(), e.getDataFim(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
