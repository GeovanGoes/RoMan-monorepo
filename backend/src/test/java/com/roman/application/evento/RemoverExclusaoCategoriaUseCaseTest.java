package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.exception.EventoParticipanteNotFoundException;
import com.roman.domain.repository.EventoParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoverExclusaoCategoriaUseCaseTest {

    @Mock
    private EventoParticipanteRepository repository;

    @InjectMocks
    private RemoverExclusaoCategoriaUseCase useCase;

    @Test
    void deve_remover_exclusao_de_categoria_com_sucesso() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        Set<UUID> categoriasExcluidas = new HashSet<>();
        categoriasExcluidas.add(categoriaId);
        EventoParticipante ep = new EventoParticipante(UUID.randomUUID(), eventoId,
                usuarioId, false, categoriasExcluidas, LocalDateTime.now());

        when(repository.findByEventoIdAndUsuarioId(eventoId, usuarioId))
                .thenReturn(Optional.of(ep));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventoParticipante result = useCase.execute(eventoId, usuarioId, categoriaId);

        assertThat(result.excluiCategoria(categoriaId)).isFalse();
        verify(repository).findByEventoIdAndUsuarioId(eventoId, usuarioId);
        verify(repository).save(ep);
    }

    @Test
    void deve_manter_outras_exclusoes_ao_remover_categoria() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaParaRemover = UUID.randomUUID();
        UUID outraCategoria = UUID.randomUUID();

        Set<UUID> categoriasExcluidas = new HashSet<>();
        categoriasExcluidas.add(categoriaParaRemover);
        categoriasExcluidas.add(outraCategoria);
        EventoParticipante ep = new EventoParticipante(UUID.randomUUID(), eventoId,
                usuarioId, false, categoriasExcluidas, LocalDateTime.now());

        when(repository.findByEventoIdAndUsuarioId(eventoId, usuarioId))
                .thenReturn(Optional.of(ep));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventoParticipante result = useCase.execute(eventoId, usuarioId, categoriaParaRemover);

        assertThat(result.excluiCategoria(categoriaParaRemover)).isFalse();
        assertThat(result.excluiCategoria(outraCategoria)).isTrue();
        verify(repository).save(ep);
    }

    @Test
    void deve_lancar_excecao_quando_vinculo_nao_encontrado() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        when(repository.findByEventoIdAndUsuarioId(eventoId, usuarioId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(eventoId, usuarioId, categoriaId))
                .isInstanceOf(EventoParticipanteNotFoundException.class);

        verify(repository).findByEventoIdAndUsuarioId(eventoId, usuarioId);
        verify(repository, never()).save(any());
    }
}
