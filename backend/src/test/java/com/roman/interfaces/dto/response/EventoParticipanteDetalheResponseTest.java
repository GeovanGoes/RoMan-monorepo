package com.roman.interfaces.dto.response;

import com.roman.application.evento.EventoParticipanteDetalhe;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventoParticipanteDetalheResponseTest {

    @Test
    void deve_mapear_todos_os_campos_do_detalhe() {
        UUID id = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        EventoParticipanteDetalhe detalhe = new EventoParticipanteDetalhe(
                id, eventoId, usuarioId,
                "João Silva", "joaosilva",
                false,
                Set.of(new EventoParticipanteDetalhe.CategoriaInfo(categoriaId, "Bebidas Alcoólicas")),
                now);

        EventoParticipanteDetalheResponse response = EventoParticipanteDetalheResponse.from(detalhe);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.eventoId()).isEqualTo(eventoId);
        assertThat(response.usuarioId()).isEqualTo(usuarioId);
        assertThat(response.nomeUsuario()).isEqualTo("João Silva");
        assertThat(response.usernameUsuario()).isEqualTo("joaosilva");
        assertThat(response.menorDeIdade()).isFalse();
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.categoriasExcluidas()).hasSize(1);

        EventoParticipanteDetalheResponse.CategoriaInfoResponse cat =
                response.categoriasExcluidas().iterator().next();
        assertThat(cat.id()).isEqualTo(categoriaId);
        assertThat(cat.nome()).isEqualTo("Bebidas Alcoólicas");
    }

    @Test
    void deve_mapear_participante_menor_de_idade_sem_categorias() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        EventoParticipanteDetalhe detalhe = new EventoParticipanteDetalhe(
                id, UUID.randomUUID(), UUID.randomUUID(),
                "Joãozinho", "joaozinho",
                true, Set.of(), now);

        EventoParticipanteDetalheResponse response = EventoParticipanteDetalheResponse.from(detalhe);

        assertThat(response.menorDeIdade()).isTrue();
        assertThat(response.categoriasExcluidas()).isEmpty();
    }

    @Test
    void deve_mapear_multiplas_categorias_excluidas() {
        UUID catId1 = UUID.randomUUID();
        UUID catId2 = UUID.randomUUID();

        EventoParticipanteDetalhe detalhe = new EventoParticipanteDetalhe(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Maria", "maria", false,
                Set.of(
                        new EventoParticipanteDetalhe.CategoriaInfo(catId1, "Bebidas"),
                        new EventoParticipanteDetalhe.CategoriaInfo(catId2, "Carnes")
                ),
                LocalDateTime.now());

        EventoParticipanteDetalheResponse response = EventoParticipanteDetalheResponse.from(detalhe);

        assertThat(response.categoriasExcluidas()).hasSize(2);
        assertThat(response.categoriasExcluidas().stream().map(EventoParticipanteDetalheResponse.CategoriaInfoResponse::nome))
                .containsExactlyInAnyOrder("Bebidas", "Carnes");
    }
}
