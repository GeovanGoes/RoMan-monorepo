package com.roman.application.evento;

import com.roman.domain.entity.Evento;
import com.roman.domain.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarEventoUseCaseTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private CriarEventoUseCase useCase;

    @Test
    void deve_criar_evento_com_sucesso() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDate inicio = LocalDate.of(2026, 6, 1);
        LocalDate fim = LocalDate.of(2026, 6, 3);
        Evento result = useCase.execute("Churrasco na Praia", "Praia de Copacabana", inicio, fim);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getNome()).isEqualTo("Churrasco na Praia");
        assertThat(result.getLocal()).isEqualTo("Praia de Copacabana");
        assertThat(result.getDataInicio()).isEqualTo(inicio);
        assertThat(result.getDataFim()).isEqualTo(fim);
        assertThat(result.getCreatedAt()).isNotNull();
        verify(repository).save(any(Evento.class));
    }
}
