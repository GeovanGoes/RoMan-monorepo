package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.CompraJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompraJpaRepository extends JpaRepository<CompraJpaEntity, UUID> {
    List<CompraJpaEntity> findByEventoId(UUID eventoId);
}
