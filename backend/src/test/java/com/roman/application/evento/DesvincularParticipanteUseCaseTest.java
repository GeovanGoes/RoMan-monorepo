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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesvincularParticipanteUseCaseTest {

    @Mock
    private EventoParticipanteRepository repository;

    @InjectMocks
    private DesvincularParticipanteUseCase useCase;

    @Test
    void deve_desvincular_participante_com_sucesso() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        EventoParticipante ep = new EventoParticipante(UUID.randomUUID(), eventoId,
                usuarioId, false, Set.of(), LocalDateTime.now());

        when(repository.findByEventoIdAndUsuarioId(eventoId, usuarioId))
                .thenReturn(Optional.of(ep));

        useCase.execute(eventoId, usuarioId);

        verify(repository).findByEventoIdAndUsuarioId(eventoId, usuarioId);
        verify(repository).delete(ep);
    }

    @Test
    void deve_lancar_excecao_quando_vinculo_nao_encontrado() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        when(repository.findByEventoIdAndUsuarioId(eventoId, usuarioId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(eventoId, usuarioId))
                .isInstanceOf(EventoParticipanteNotFoundException.class);

        verify(repository).findByEventoIdAndUsuarioId(eventoId, usuarioId);
        verify(repository, never()).delete(any());
    }
}
