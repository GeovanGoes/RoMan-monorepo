package com.roman.application.evento;

import com.roman.domain.entity.Compra;
import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.entity.Participante;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.CompraRepository;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularRateioUseCaseTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private EventoParticipanteRepository eventoParticipanteRepository;
    @Mock private CompraRepository compraRepository;
    @Mock private ParticipanteRepository participanteRepository;

    @InjectMocks
    private CalcularRateioUseCase useCase;

    @Test
    void deve_lancar_excecao_quando_evento_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(eventoId))
                .isInstanceOf(EventoNotFoundException.class);
    }

    @Test
    void deve_retornar_saldo_zero_quando_nao_ha_compras() {
        UUID eventoId = UUID.randomUUID();
        UUID p1Id = UUID.randomUUID();

        EventoParticipante ep1 = EventoParticipante.criar(eventoId, p1Id, false);
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(eventoParticipanteRepository.findByEventoId(eventoId)).thenReturn(List.of(ep1));
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of());
        when(participanteRepository.findById(p1Id)).thenReturn(Optional.of(Participante.criar("Ana", "ana")));

        List<RateioItem> resultado = useCase.execute(eventoId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).totalDevido()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.get(0).totalPago()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.get(0).saldo()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void menor_de_idade_nao_gera_debito() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID adultoPid = UUID.randomUUID();
        UUID menorPid = UUID.randomUUID();
        UUID pagadorId = UUID.randomUUID();

        EventoParticipante adulto = EventoParticipante.criar(eventoId, adultoPid, false);
        EventoParticipante menor = EventoParticipante.criar(eventoId, menorPid, true);
        Compra compra = Compra.criar("Bebida", new BigDecimal("100.00"), eventoId, categoriaId, Set.of(pagadorId));

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(eventoParticipanteRepository.findByEventoId(eventoId)).thenReturn(List.of(adulto, menor));
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of(compra));
        when(participanteRepository.findById(adultoPid)).thenReturn(Optional.of(Participante.criar("Bruno", "bruno")));
        when(participanteRepository.findById(menorPid)).thenReturn(Optional.of(Participante.criar("Carlos", "carlos")));

        List<RateioItem> resultado = useCase.execute(eventoId);

        RateioItem itemAdulto = resultado.stream().filter(r -> r.participanteId().equals(adultoPid)).findFirst().orElseThrow();
        RateioItem itemMenor = resultado.stream().filter(r -> r.participanteId().equals(menorPid)).findFirst().orElseThrow();

        assertThat(itemAdulto.totalDevido()).isEqualByComparingTo("100.00");
        assertThat(itemMenor.totalDevido()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void participante_com_exclusao_de_categoria_nao_paga_aquela_compra() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaAlcool = UUID.randomUUID();
        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();
        UUID pagadorId = UUID.randomUUID();

        EventoParticipante ep1 = EventoParticipante.criar(eventoId, p1Id, false);
        EventoParticipante ep2 = EventoParticipante.criar(eventoId, p2Id, false);
        ep2.adicionarExclusaoCategoria(categoriaAlcool);

        Compra compra = Compra.criar("Cerveja", new BigDecimal("60.00"), eventoId, categoriaAlcool, Set.of(pagadorId));

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(eventoParticipanteRepository.findByEventoId(eventoId)).thenReturn(List.of(ep1, ep2));
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of(compra));
        when(participanteRepository.findById(p1Id)).thenReturn(Optional.of(Participante.criar("Ana", "ana")));
        when(participanteRepository.findById(p2Id)).thenReturn(Optional.of(Participante.criar("Bia", "bia")));

        List<RateioItem> resultado = useCase.execute(eventoId);

        RateioItem item1 = resultado.stream().filter(r -> r.participanteId().equals(p1Id)).findFirst().orElseThrow();
        RateioItem item2 = resultado.stream().filter(r -> r.participanteId().equals(p2Id)).findFirst().orElseThrow();

        assertThat(item1.totalDevido()).isEqualByComparingTo("60.00");
        assertThat(item2.totalDevido()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void pagador_que_pagou_mais_do_que_devia_tem_saldo_positivo() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();

        EventoParticipante ep1 = EventoParticipante.criar(eventoId, p1Id, false);
        EventoParticipante ep2 = EventoParticipante.criar(eventoId, p2Id, false);

        // p1 pagou R$100 mas só deve R$50 (dividido entre 2)
        Compra compra = Compra.criar("Compra", new BigDecimal("100.00"), eventoId, categoriaId, Set.of(p1Id));

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(eventoParticipanteRepository.findByEventoId(eventoId)).thenReturn(List.of(ep1, ep2));
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of(compra));
        when(participanteRepository.findById(p1Id)).thenReturn(Optional.of(Participante.criar("Ana", "ana")));
        when(participanteRepository.findById(p2Id)).thenReturn(Optional.of(Participante.criar("Bruno", "bruno")));

        List<RateioItem> resultado = useCase.execute(eventoId);

        RateioItem itemP1 = resultado.stream().filter(r -> r.participanteId().equals(p1Id)).findFirst().orElseThrow();
        RateioItem itemP2 = resultado.stream().filter(r -> r.participanteId().equals(p2Id)).findFirst().orElseThrow();

        assertThat(itemP1.totalPago()).isEqualByComparingTo("100.00");
        assertThat(itemP1.totalDevido()).isEqualByComparingTo("50.00");
        assertThat(itemP1.saldo()).isEqualByComparingTo("50.00"); // a receber

        assertThat(itemP2.totalPago()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(itemP2.totalDevido()).isEqualByComparingTo("50.00");
        assertThat(itemP2.saldo()).isEqualByComparingTo("-50.00"); // deve pagar
    }

    @Test
    void dois_pagadores_dividem_credito_igualmente() {
        UUID eventoId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();

        EventoParticipante ep1 = EventoParticipante.criar(eventoId, p1Id, false);
        EventoParticipante ep2 = EventoParticipante.criar(eventoId, p2Id, false);

        // p1 e p2 pagaram juntos R$100
        Compra compra = Compra.criar("Compra", new BigDecimal("100.00"), eventoId, categoriaId, Set.of(p1Id, p2Id));

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(eventoParticipanteRepository.findByEventoId(eventoId)).thenReturn(List.of(ep1, ep2));
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of(compra));
        when(participanteRepository.findById(p1Id)).thenReturn(Optional.of(Participante.criar("Ana", "ana")));
        when(participanteRepository.findById(p2Id)).thenReturn(Optional.of(Participante.criar("Bruno", "bruno")));

        List<RateioItem> resultado = useCase.execute(eventoId);

        for (RateioItem item : resultado) {
            assertThat(item.totalPago()).isEqualByComparingTo("50.00");
            assertThat(item.totalDevido()).isEqualByComparingTo("50.00");
            assertThat(item.saldo()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Test
    void nome_participante_e_incluido_no_resultado() {
        UUID eventoId = UUID.randomUUID();
        UUID pid = UUID.randomUUID();

        EventoParticipante ep = EventoParticipante.criar(eventoId, pid, false);
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(eventoParticipanteRepository.findByEventoId(eventoId)).thenReturn(List.of(ep));
        when(compraRepository.findByEventoId(eventoId)).thenReturn(List.of());
        when(participanteRepository.findById(pid)).thenReturn(Optional.of(Participante.criar("Maria", "maria")));

        List<RateioItem> resultado = useCase.execute(eventoId);

        assertThat(resultado.get(0).nomeParticipante()).isEqualTo("Maria");
        assertThat(resultado.get(0).participanteId()).isEqualTo(pid);
    }
}
