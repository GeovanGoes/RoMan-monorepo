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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarEventoUseCaseTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private AtualizarEventoUseCase useCase;

    @Test
    void deve_atualizar_evento_com_sucesso() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Evento eventoExistente = new Evento(id, "Nome Antigo", "Local Antigo",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3), now, now);

        when(repository.findById(id)).thenReturn(Optional.of(eventoExistente));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDate novoInicio = LocalDate.of(2026, 7, 10);
        LocalDate novoFim = LocalDate.of(2026, 7, 15);
        Evento result = useCase.execute(id, "Nome Novo", "Local Novo", novoInicio, novoFim);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNome()).isEqualTo("Nome Novo");
        assertThat(result.getLocal()).isEqualTo("Local Novo");
        assertThat(result.getDataInicio()).isEqualTo(novoInicio);
        assertThat(result.getDataFim()).isEqualTo(novoFim);
        verify(repository).findById(id);
        verify(repository).save(eventoExistente);
    }

    @Test
    void deve_lancar_excecao_quando_evento_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, "Nome", "Local",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3)))
                .isInstanceOf(EventoNotFoundException.class);

        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }
}
