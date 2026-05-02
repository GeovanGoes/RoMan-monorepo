package com.roman.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Participante {

    private final UUID id;
    private String nome;
    private String username;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Participante(UUID id, String nome, String username,
                        LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Participante criar(String nome, String username) {
        LocalDateTime now = LocalDateTime.now();
        return new Participante(UUID.randomUUID(), nome, username, now, now, null);
    }

    public void atualizar(String nome, String username) {
        this.nome = nome;
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }

    public void remover() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return deletedAt == null;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}
