package com.roman.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {
    private final UUID id;
    private final UUID usuarioId;
    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private boolean revogado;
    private final LocalDateTime createdAt;

    public RefreshToken(UUID id, UUID usuarioId, String tokenHash, LocalDateTime expiresAt,
                        boolean revogado, LocalDateTime createdAt) {
        this.id = id; this.usuarioId = usuarioId; this.tokenHash = tokenHash;
        this.expiresAt = expiresAt; this.revogado = revogado; this.createdAt = createdAt;
    }

    public static RefreshToken criar(UUID usuarioId, String tokenHash, long expirationSeconds) {
        LocalDateTime now = LocalDateTime.now();
        return new RefreshToken(UUID.randomUUID(), usuarioId, tokenHash,
                now.plusSeconds(expirationSeconds), false, now);
    }

    public boolean isValido() {
        return !revogado && expiresAt.isAfter(LocalDateTime.now());
    }

    public void revogar() { this.revogado = true; }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isRevogado() { return revogado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
