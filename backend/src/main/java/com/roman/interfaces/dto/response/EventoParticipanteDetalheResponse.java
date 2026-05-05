package com.roman.interfaces.dto.response;

import com.roman.application.evento.EventoParticipanteDetalhe;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record EventoParticipanteDetalheResponse(
        UUID id,
        UUID eventoId,
        UUID usuarioId,
        String nomeUsuario,
        String usernameUsuario,
        boolean menorDeIdade,
        Set<CategoriaInfoResponse> categoriasExcluidas,
        LocalDateTime createdAt
) {
    public record CategoriaInfoResponse(UUID id, String nome) {}

    public static EventoParticipanteDetalheResponse from(EventoParticipanteDetalhe d) {
        Set<CategoriaInfoResponse> cats = d.categoriasExcluidas().stream()
                .map(c -> new CategoriaInfoResponse(c.id(), c.nome()))
                .collect(Collectors.toSet());
        return new EventoParticipanteDetalheResponse(
                d.id(), d.eventoId(), d.usuarioId(),
                d.nomeUsuario(), d.usernameUsuario(),
                d.menorDeIdade(), cats, d.createdAt());
    }
}
