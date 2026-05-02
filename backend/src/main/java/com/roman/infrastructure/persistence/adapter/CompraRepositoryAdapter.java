package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.Compra;
import com.roman.domain.repository.CompraRepository;
import com.roman.infrastructure.persistence.entity.CompraJpaEntity;
import com.roman.infrastructure.persistence.entity.ParticipanteJpaEntity;
import com.roman.infrastructure.persistence.repository.CompraJpaRepository;
import com.roman.infrastructure.persistence.repository.ParticipanteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CompraRepositoryAdapter implements CompraRepository {

    private final CompraJpaRepository jpaRepository;
    private final ParticipanteJpaRepository participanteJpaRepository;

    public CompraRepositoryAdapter(CompraJpaRepository jpaRepository,
                                   ParticipanteJpaRepository participanteJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.participanteJpaRepository = participanteJpaRepository;
    }

    @Override
    public Compra save(Compra compra) {
        return toDomain(jpaRepository.save(toEntity(compra)));
    }

    @Override
    public Optional<Compra> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Compra> findByEventoId(UUID eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    private CompraJpaEntity toEntity(Compra c) {
        Set<ParticipanteJpaEntity> pagadores = c.getPagadoresIds().stream()
                .map(id -> participanteJpaRepository.getReferenceById(id))
                .collect(Collectors.toSet());

        return CompraJpaEntity.builder()
                .id(c.getId())
                .descricao(c.getDescricao())
                .valor(c.getValor())
                .eventoId(c.getEventoId())
                .categoriaId(c.getCategoriaId())
                .pagadores(pagadores)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private Compra toDomain(CompraJpaEntity e) {
        Set<UUID> pagadoresIds = e.getPagadores().stream()
                .map(ParticipanteJpaEntity::getId)
                .collect(Collectors.toSet());

        return new Compra(e.getId(), e.getDescricao(), e.getValor(), e.getEventoId(),
                e.getCategoriaId(), pagadoresIds, e.getCreatedAt(), e.getUpdatedAt());
    }
}
