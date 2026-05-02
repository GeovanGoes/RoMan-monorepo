package com.roman.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class CategoriaConsumo {

    private final UUID id;
    private String nome;
    private String descricao;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoriaConsumo(UUID id, String nome, String descricao,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CategoriaConsumo criar(String nome, String descricao) {
        LocalDateTime now = LocalDateTime.now();
        return new CategoriaConsumo(UUID.randomUUID(), nome, descricao, now, now);
    }

    public void atualizar(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
