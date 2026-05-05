package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.Compra;
import com.roman.domain.repository.CompraRepository;
import com.roman.infrastructure.persistence.entity.CompraJpaEntity;
import com.roman.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.roman.infrastructure.persistence.repository.CompraJpaRepository;
import com.roman.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CompraRepositoryAdapter implements CompraRepository {

    private final CompraJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public CompraRepositoryAdapter(CompraJpaRepository jpaRepository,
                                   UsuarioJpaRepository usuarioJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
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
        Set<UsuarioJpaEntity> pagadores = c.getPagadoresIds().stream()
                .map(id -> usuarioJpaRepository.getReferenceById(id))
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
                .map(UsuarioJpaEntity::getId)
                .collect(Collectors.toSet());

        return new Compra(e.getId(), e.getDescricao(), e.getValor(), e.getEventoId(),
                e.getCategoriaId(), pagadoresIds, e.getCreatedAt(), e.getUpdatedAt());
    }
}
