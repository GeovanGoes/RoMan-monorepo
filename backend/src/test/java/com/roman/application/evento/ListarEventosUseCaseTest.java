package com.roman.application.evento;

import com.roman.domain.entity.Evento;
import com.roman.domain.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarEventosUseCaseTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private ListarEventosUseCase useCase;

    @Test
    void deve_retornar_lista_de_eventos() {
        LocalDateTime now = LocalDateTime.now();
        Evento evento1 = new Evento(UUID.randomUUID(), "Evento 1", "Local 1",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3), now, now);
        Evento evento2 = new Evento(UUID.randomUUID(), "Evento 2", "Local 2",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), now, now);

        when(repository.findAll()).thenReturn(List.of(evento1, evento2));

        List<Evento> result = useCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(evento1, evento2);
        verify(repository).findAll();
    }

    @Test
    void deve_retornar_lista_vazia_quando_nao_ha_eventos() {
        when(repository.findAll()).thenReturn(List.of());

        List<Evento> result = useCase.execute();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }
}
