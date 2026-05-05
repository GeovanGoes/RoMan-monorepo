package com.roman.application.evento;

import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoverEventoUseCaseTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private RemoverEventoUseCase useCase;

    @Test
    void deve_remover_evento_com_sucesso() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        useCase.execute(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deve_lancar_excecao_quando_evento_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(EventoNotFoundException.class);

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }
}
