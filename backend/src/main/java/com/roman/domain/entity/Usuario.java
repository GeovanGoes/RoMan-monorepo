package com.roman.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Usuario {
    private final UUID id;
    private String nome;
    private String username;
    private String email;
    private String telefone;
    private String senhaHash;
    private PerfilUsuario perfil;
    private boolean senhaProvisoria;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Usuario(UUID id, String nome, String username, String email, String telefone,
                   String senhaHash, PerfilUsuario perfil, boolean senhaProvisoria,
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id; this.nome = nome; this.username = username; this.email = email;
        this.telefone = telefone; this.senhaHash = senhaHash; this.perfil = perfil;
        this.senhaProvisoria = senhaProvisoria; this.createdAt = createdAt;
        this.updatedAt = updatedAt; this.deletedAt = deletedAt;
    }

    public static Usuario criar(String nome, String username, String email, String telefone,
                                String senhaHash, PerfilUsuario perfil) {
        LocalDateTime now = LocalDateTime.now();
        return new Usuario(UUID.randomUUID(), nome, username, email, telefone, senhaHash, perfil,
                true, now, now, null);
    }

    public void alterarSenha(String novoHash) {
        this.senhaHash = novoHash;
        this.senhaProvisoria = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void atualizar(String nome, String email, String telefone) {
        this.nome = nome; this.email = email; this.telefone = telefone;
        this.updatedAt = LocalDateTime.now();
    }

    public void remover() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAtivo() { return deletedAt == null; }

    // getters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSenhaHash() { return senhaHash; }
    public PerfilUsuario getPerfil() { return perfil; }
    public boolean isSenhaProvisoria() { return senhaProvisoria; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}
