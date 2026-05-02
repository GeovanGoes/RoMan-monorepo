package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.EventoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventoJpaRepository extends JpaRepository<EventoJpaEntity, UUID> {
}
