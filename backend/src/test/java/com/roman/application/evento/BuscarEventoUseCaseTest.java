package com.roman.application.evento;

import com.roman.domain.entity.Evento;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarEventoUseCaseTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private BuscarEventoUseCase useCase;

    @Test
    void deve_buscar_evento_por_id_com_sucesso() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Evento evento = new Evento(id, "Churrasco", "Praia",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3), now, now);

        when(repository.findById(id)).thenReturn(Optional.of(evento));

        Evento result = useCase.execute(id);

        assertThat(result).isEqualTo(evento);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNome()).isEqualTo("Churrasco");
        verify(repository).findById(id);
    }

    @Test
    void deve_lancar_excecao_quando_evento_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(EventoNotFoundException.class);

        verify(repository).findById(id);
    }
}
