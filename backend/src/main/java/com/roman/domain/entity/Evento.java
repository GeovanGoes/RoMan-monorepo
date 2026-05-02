package com.roman.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Evento {

    private final UUID id;
    private String nome;
    private String local;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Evento(UUID id, String nome, String local, LocalDate dataInicio, LocalDate dataFim,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.local = local;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Evento criar(String nome, String local, LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime now = LocalDateTime.now();
        return new Evento(UUID.randomUUID(), nome, local, dataInicio, dataFim, now, now);
    }

    public void atualizar(String nome, String local, LocalDate dataInicio, LocalDate dataFim) {
        this.nome = nome;
        this.local = local;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getLocal() { return local; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
