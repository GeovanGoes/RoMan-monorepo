package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByUsername(String username);
    Optional<UsuarioJpaEntity> findByEmailHash(String emailHash);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, UUID id);
    List<UsuarioJpaEntity> findAllByDeletedAtIsNull();
}
