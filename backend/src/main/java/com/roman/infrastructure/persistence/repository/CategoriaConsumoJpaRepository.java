package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.CategoriaConsumoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaConsumoJpaRepository extends JpaRepository<CategoriaConsumoJpaEntity, UUID> {
}
