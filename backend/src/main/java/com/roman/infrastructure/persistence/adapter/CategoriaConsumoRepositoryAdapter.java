package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.repository.CategoriaConsumoRepository;
import com.roman.infrastructure.persistence.entity.CategoriaConsumoJpaEntity;
import com.roman.infrastructure.persistence.repository.CategoriaConsumoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoriaConsumoRepositoryAdapter implements CategoriaConsumoRepository {

    private final CategoriaConsumoJpaRepository jpaRepository;

    public CategoriaConsumoRepositoryAdapter(CategoriaConsumoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CategoriaConsumo save(CategoriaConsumo categoria) {
        return toDomain(jpaRepository.save(toEntity(categoria)));
    }

    @Override
    public Optional<CategoriaConsumo> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CategoriaConsumo> findAll() {
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

    private CategoriaConsumoJpaEntity toEntity(CategoriaConsumo c) {
        return CategoriaConsumoJpaEntity.builder()
                .id(c.getId())
                .nome(c.getNome())
                .descricao(c.getDescricao())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CategoriaConsumo toDomain(CategoriaConsumoJpaEntity e) {
        return new CategoriaConsumo(e.getId(), e.getNome(), e.getDescricao(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
}
