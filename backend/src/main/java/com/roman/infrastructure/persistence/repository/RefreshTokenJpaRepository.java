package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {
    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revogado = true WHERE r.usuarioId = :usuarioId")
    void revokeAllByUsuarioId(UUID usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.expiresAt < :now")
    void deleteByExpiresAtBefore(LocalDateTime now);
}
