package com.roman.application.evento;

import com.roman.domain.entity.Compra;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.CompraRepository;
import com.roman.domain.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarComprasUseCaseTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private CompraRepository compraRepository;

    @InjectMocks
    private ListarComprasUseCase useCase;

    @Test
    void deve_listar_compras_do_evento_com_sucesso() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Compra compra1 = new Compra(UUID.randomUUID(), "Cerveja", new BigDecimal("50.00"),
                eventoId, categoriaId, Set.of(), now, now);
        Compra compra2 = new Compra(UUID.randomUUID(), "Carne", new BigDecimal("120.00"),
                eventoId, categoriaId, Set.of(), now, now);

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of(compra1, compra2));

        List<Compra> result = useCase.execute(eventoId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(compra1, compra2);
        verify(eventoRepository).existsById(eventoId);
        verify(compraRepository).findByEventoId(eventoId);
    }

    @Test
    void deve_retornar_lista_vazia_quando_evento_nao_tem_compras() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of());

        List<Compra> result = useCase.execute(eventoId);

        assertThat(result).isEmpty();
        verify(eventoRepository).existsById(eventoId);
        verify(compraRepository).findByEventoId(eventoId);
    }

    @Test
    void deve_lancar_excecao_quando_evento_nao_encontrado() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(eventoId))
                .isInstanceOf(EventoNotFoundException.class);

        verify(eventoRepository).existsById(eventoId);
        verify(compraRepository, never()).findByEventoId(any());
    }
}
