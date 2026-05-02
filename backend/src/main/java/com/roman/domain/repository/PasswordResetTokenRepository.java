package com.roman.domain.repository;

import com.roman.domain.entity.PasswordResetToken;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void invalidateAllByUsuarioId(UUID usuarioId);
}
