package com.roman.application.evento;

import com.roman.domain.entity.Compra;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import com.roman.domain.repository.CompraRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdicionarCompraUseCaseTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private CategoriaConsumoRepository categoriaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CompraRepository compraRepository;

    @InjectMocks
    private AdicionarCompraUseCase useCase;

    @Test
    void deve_adicionar_compra_ao_evento() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID pagadorId = UUID.randomUUID();

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(categoriaRepository.existsById(categoriaId)).thenReturn(true);
        when(usuarioRepository.findById(pagadorId)).thenReturn(
                Optional.of(Usuario.criarConvidado("João", "joao")));
        when(compraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Compra result = useCase.execute("Cerveja", new BigDecimal("150.00"), eventoId, categoriaId, Set.of(pagadorId));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescricao()).isEqualTo("Cerveja");
        assertThat(result.getValor()).isEqualByComparingTo("150.00");
        assertThat(result.getPagadoresIds()).containsExactly(pagadorId);
    }

    @Test
    void deve_lancar_excecao_quando_evento_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute("Item", BigDecimal.TEN, eventoId, UUID.randomUUID(), Set.of()))
                .isInstanceOf(EventoNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_categoria_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(categoriaRepository.existsById(categoriaId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute("Item", BigDecimal.TEN, eventoId, categoriaId, Set.of()))
                .isInstanceOf(CategoriaConsumoNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_pagador_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID pagadorId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(categoriaRepository.existsById(categoriaId)).thenReturn(true);
        when(usuarioRepository.findById(pagadorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("Item", BigDecimal.TEN, eventoId, categoriaId, Set.of(pagadorId)))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }
}
