package com.roman.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Compra {

    private final UUID id;
    private String descricao;
    private BigDecimal valor;
    private final UUID eventoId;
    private UUID categoriaId;
    private final Set<UUID> pagadoresIds;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Compra(UUID id, String descricao, BigDecimal valor, UUID eventoId, UUID categoriaId,
                  Set<UUID> pagadoresIds, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.eventoId = eventoId;
        this.categoriaId = categoriaId;
        this.pagadoresIds = pagadoresIds != null ? new HashSet<>(pagadoresIds) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Compra criar(String descricao, BigDecimal valor, UUID eventoId,
                               UUID categoriaId, Set<UUID> pagadoresIds) {
        LocalDateTime now = LocalDateTime.now();
        return new Compra(UUID.randomUUID(), descricao, valor, eventoId, categoriaId, pagadoresIds, now, now);
    }

    public UUID getId() { return id; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public UUID getEventoId() { return eventoId; }
    public UUID getCategoriaId() { return categoriaId; }
    public Set<UUID> getPagadoresIds() { return new HashSet<>(pagadoresIds); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
