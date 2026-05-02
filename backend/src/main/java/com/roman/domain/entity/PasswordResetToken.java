package com.roman.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetToken {
    private final UUID id;
    private final UUID usuarioId;
    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private boolean usado;
    private final LocalDateTime createdAt;

    public PasswordResetToken(UUID id, UUID usuarioId, String tokenHash, LocalDateTime expiresAt,
                              boolean usado, LocalDateTime createdAt) {
        this.id = id; this.usuarioId = usuarioId; this.tokenHash = tokenHash;
        this.expiresAt = expiresAt; this.usado = usado; this.createdAt = createdAt;
    }

    public static PasswordResetToken criar(UUID usuarioId, String tokenHash) {
        LocalDateTime now = LocalDateTime.now();
        return new PasswordResetToken(UUID.randomUUID(), usuarioId, tokenHash,
                now.plusHours(1), false, now);
    }

    public boolean isValido() {
        return !usado && expiresAt.isAfter(LocalDateTime.now());
    }

    public void marcarComoUsado() { this.usado = true; }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsado() { return usado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
