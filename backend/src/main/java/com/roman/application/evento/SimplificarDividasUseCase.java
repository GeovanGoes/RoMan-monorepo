package com.roman.application.evento;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Simplifica o rateio de um evento numa lista mínima de transferências
 * sugeridas entre participantes (algoritmo guloso de "settle up"):
 * a cada passo, casa o maior devedor com o maior credor até que os
 * saldos se esgotem.
 *
 * <p>Como cada parcela do rateio é arredondada individualmente em
 * {@link CalcularRateioUseCase}, a soma dos saldos de um evento pode não
 * fechar exatamente em zero. Um resíduo isolado (sem contraparte que o
 * compense) simplesmente não gera transferência — ele já fica visível
 * como saldo residual na própria tela de rateio.</p>
 */
@Service
public class SimplificarDividasUseCase {

    private final CalcularRateioUseCase calcularRateioUseCase;

    public SimplificarDividasUseCase(CalcularRateioUseCase calcularRateioUseCase) {
        this.calcularRateioUseCase = calcularRateioUseCase;
    }

    public List<TransferenciaSugerida> execute(UUID eventoId) {
        List<RateioItem> rateio = calcularRateioUseCase.execute(eventoId);
        return simplificar(rateio);
    }

    private List<TransferenciaSugerida> simplificar(List<RateioItem> rateio) {
        List<Saldo> credores = new ArrayList<>();
        List<Saldo> devedores = new ArrayList<>();

        for (RateioItem item : rateio) {
            int comparacao = item.saldo().compareTo(BigDecimal.ZERO);
            if (comparacao > 0) {
                credores.add(new Saldo(item.usuarioId(), item.nomeParticipante(), item.saldo()));
            } else if (comparacao < 0) {
                devedores.add(new Saldo(item.usuarioId(), item.nomeParticipante(), item.saldo().negate()));
            }
        }

        Comparator<Saldo> ordemDecrescente = Comparator
                .comparing((Saldo s) -> s.valor)
                .thenComparing(s -> s.usuarioId, Comparator.reverseOrder())
                .reversed();

        List<TransferenciaSugerida> transferencias = new ArrayList<>();
        while (!credores.isEmpty() && !devedores.isEmpty()) {
            credores.sort(ordemDecrescente);
            devedores.sort(ordemDecrescente);

            Saldo credor = credores.get(0);
            Saldo devedor = devedores.get(0);
            BigDecimal valorTransferencia = credor.valor.min(devedor.valor);

            transferencias.add(new TransferenciaSugerida(
                    devedor.usuarioId, devedor.nome,
                    credor.usuarioId, credor.nome,
                    valorTransferencia
            ));

            credor.valor = credor.valor.subtract(valorTransferencia);
            devedor.valor = devedor.valor.subtract(valorTransferencia);

            if (credor.valor.compareTo(BigDecimal.ZERO) == 0) {
                credores.remove(0);
            }
            if (devedor.valor.compareTo(BigDecimal.ZERO) == 0) {
                devedores.remove(0);
            }
        }

        return transferencias;
    }

    private static final class Saldo {
        private final UUID usuarioId;
        private final String nome;
        private BigDecimal valor;

        private Saldo(UUID usuarioId, String nome, BigDecimal valor) {
            this.usuarioId = usuarioId;
            this.nome = nome;
            this.valor = valor;
        }
    }
}
