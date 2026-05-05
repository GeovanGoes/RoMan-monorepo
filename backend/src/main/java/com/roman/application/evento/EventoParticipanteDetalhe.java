package com.roman.application.evento;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record EventoParticipanteDetalhe(
        UUID id,
        UUID eventoId,
        UUID usuarioId,
        String nomeUsuario,
        String usernameUsuario,
        boolean menorDeIdade,
        Set<CategoriaInfo> categoriasExcluidas,
        LocalDateTime createdAt
) {
    public record CategoriaInfo(UUID id, String nome) {}
}
