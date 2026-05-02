package com.roman.domain.repository;

import com.roman.domain.entity.RefreshToken;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeAllByUsuarioId(UUID usuarioId);
    void deleteExpired();
}
