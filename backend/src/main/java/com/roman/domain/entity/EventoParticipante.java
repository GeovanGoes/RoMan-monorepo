package com.roman.domain.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EventoParticipante {

    private final UUID id;
    private final UUID eventoId;
    private final UUID participanteId;
    private boolean menorDeIdade;
    private final Set<UUID> categoriasExcluidas;
    private final LocalDateTime createdAt;

    public EventoParticipante(UUID id, UUID eventoId, UUID participanteId, boolean menorDeIdade,
                              Set<UUID> categoriasExcluidas, LocalDateTime createdAt) {
        this.id = id;
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.menorDeIdade = menorDeIdade;
        this.categoriasExcluidas = categoriasExcluidas != null ? new HashSet<>(categoriasExcluidas) : new HashSet<>();
        this.createdAt = createdAt;
    }

    public static EventoParticipante criar(UUID eventoId, UUID participanteId, boolean menorDeIdade) {
        return new EventoParticipante(UUID.randomUUID(), eventoId, participanteId, menorDeIdade,
                new HashSet<>(), LocalDateTime.now());
    }

    public void adicionarExclusaoCategoria(UUID categoriaId) {
        this.categoriasExcluidas.add(categoriaId);
    }

    public void removerExclusaoCategoria(UUID categoriaId) {
        this.categoriasExcluidas.remove(categoriaId);
    }

    public boolean excluiCategoria(UUID categoriaId) {
        return categoriasExcluidas.contains(categoriaId);
    }

    public UUID getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public UUID getParticipanteId() { return participanteId; }
    public boolean isMenorDeIdade() { return menorDeIdade; }
    public Set<UUID> getCategoriasExcluidas() { return new HashSet<>(categoriasExcluidas); }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
