package com.roman.application.evento;

import com.roman.domain.exception.EventoNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimplificarDividasUseCaseTest {

    @Mock
    private CalcularRateioUseCase calcularRateioUseCase;

    @InjectMocks
    private SimplificarDividasUseCase useCase;

    private static RateioItem item(UUID id, String nome, String saldo) {
        // totalDevido/totalPago não importam para a simplificação, só o saldo
        return new RateioItem(id, nome, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal(saldo));
    }

    @Test
    void deve_propagar_excecao_quando_evento_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenThrow(new EventoNotFoundException(eventoId));

        assertThatThrownBy(() -> useCase.execute(eventoId))
                .isInstanceOf(EventoNotFoundException.class);
    }

    @Test
    void deve_retornar_lista_vazia_quando_nao_ha_participantes() {
        UUID eventoId = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of());

        assertThat(useCase.execute(eventoId)).isEmpty();
    }

    @Test
    void deve_retornar_lista_vazia_quando_todos_os_saldos_sao_zero() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "0.00"),
                item(b, "Bruno", "0.00")
        ));

        assertThat(useCase.execute(eventoId)).isEmpty();
    }

    @Test
    void duas_pessoas_uma_deve_para_outra() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "-50.00"),
                item(b, "Bruno", "50.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).deId()).isEqualTo(a);
        assertThat(resultado.get(0).paraId()).isEqualTo(b);
        assertThat(resultado.get(0).valor()).isEqualByComparingTo("50.00");
    }

    @Test
    void um_devedor_paga_dois_credores_do_maior_para_o_menor() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "-90.00"),
                item(b, "Bruno", "30.00"),
                item(c, "Carla", "60.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).deId()).isEqualTo(a);
        assertThat(resultado.get(0).paraId()).isEqualTo(c);
        assertThat(resultado.get(0).valor()).isEqualByComparingTo("60.00");
        assertThat(resultado.get(1).deId()).isEqualTo(a);
        assertThat(resultado.get(1).paraId()).isEqualTo(b);
        assertThat(resultado.get(1).valor()).isEqualByComparingTo("30.00");
    }

    @Test
    void multiplos_credores_e_devedores_gera_numero_minimo_de_transacoes() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        UUID d = UUID.randomUUID();
        // A e D se compensam exatamente, B e C se compensam exatamente:
        // deve gerar 2 transações, não 3 (n-1 ingênuo para 4 participantes)
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "-30.00"),
                item(b, "Bruno", "-10.00"),
                item(c, "Carla", "10.00"),
                item(d, "Duda", "30.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(TransferenciaSugerida::deId, TransferenciaSugerida::paraId, TransferenciaSugerida::valor)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(a, d, new BigDecimal("30.00")),
                        org.assertj.core.groups.Tuple.tuple(b, c, new BigDecimal("10.00"))
                );
    }

    @Test
    void empate_entre_devedores_e_resolvido_por_ordem_deterministica_de_id() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID b = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID c = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(b, "Bruno", "-20.00"),
                item(a, "Ana", "-20.00"),
                item(c, "Carla", "40.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).deId()).isEqualTo(a);
        assertThat(resultado.get(1).deId()).isEqualTo(b);
    }

    @Test
    void residuo_isolado_que_nao_tem_par_correspondente_nao_gera_transferencia() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        // soma dos saldos não fecha em zero (resíduo de arredondamento entre compras diferentes)
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "-10.01"),
                item(b, "Bruno", "10.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).valor()).isEqualByComparingTo("10.00");
    }

    @Test
    void soma_das_transferencias_por_devedor_bate_com_saldo_original_quando_soma_fecha_em_zero() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        UUID d = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "-30.00"),
                item(b, "Bruno", "-10.00"),
                item(c, "Carla", "10.00"),
                item(d, "Duda", "30.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        BigDecimal totalSaidaA = resultado.stream()
                .filter(t -> t.deId().equals(a))
                .map(TransferenciaSugerida::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSaidaB = resultado.stream()
                .filter(t -> t.deId().equals(b))
                .map(TransferenciaSugerida::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalSaidaA).isEqualByComparingTo("30.00");
        assertThat(totalSaidaB).isEqualByComparingTo("10.00");
    }

    @Test
    void participante_com_saldo_zero_nao_aparece_em_nenhuma_transferencia() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "-20.00"),
                item(b, "Bruno", "0.00"),
                item(c, "Carla", "20.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado).noneMatch(t -> t.deId().equals(b) || t.paraId().equals(b));
    }

    @Test
    void nao_gera_transferencia_de_valor_zero() {
        UUID eventoId = UUID.randomUUID();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(calcularRateioUseCase.execute(eventoId)).thenReturn(List.of(
                item(a, "Ana", "-50.00"),
                item(b, "Bruno", "50.00")
        ));

        List<TransferenciaSugerida> resultado = useCase.execute(eventoId);

        assertThat(resultado).noneMatch(t -> t.valor().compareTo(BigDecimal.ZERO) == 0);
    }
}
