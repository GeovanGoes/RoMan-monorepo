package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.ParticipanteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipanteJpaRepository extends JpaRepository<ParticipanteJpaEntity, UUID> {
    List<ParticipanteJpaEntity> findAllByDeletedAtIsNull();
    boolean existsByUsernameAndDeletedAtIsNull(String username);
    boolean existsByUsernameAndDeletedAtIsNullAndIdNot(String username, UUID id);
}
