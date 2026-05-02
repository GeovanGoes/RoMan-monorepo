package com.roman.infrastructure.persistence.repository;

import com.roman.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {
    Optional<PasswordResetTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetTokenJpaEntity p SET p.usado = true WHERE p.usuarioId = :usuarioId")
    void invalidateAllByUsuarioId(UUID usuarioId);
}
