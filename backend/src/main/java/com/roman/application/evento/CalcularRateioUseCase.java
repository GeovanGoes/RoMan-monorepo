package com.roman.application.evento;

import com.roman.domain.entity.Compra;
import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.entity.Participante;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.CompraRepository;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CalcularRateioUseCase {

    private final EventoRepository eventoRepository;
    private final EventoParticipanteRepository eventoParticipanteRepository;
    private final CompraRepository compraRepository;
    private final ParticipanteRepository participanteRepository;

    public CalcularRateioUseCase(EventoRepository eventoRepository,
                                 EventoParticipanteRepository eventoParticipanteRepository,
                                 CompraRepository compraRepository,
                                 ParticipanteRepository participanteRepository) {
        this.eventoRepository = eventoRepository;
        this.eventoParticipanteRepository = eventoParticipanteRepository;
        this.compraRepository = compraRepository;
        this.participanteRepository = participanteRepository;
    }

    public List<RateioItem> execute(UUID eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }

        List<EventoParticipante> eps = eventoParticipanteRepository.findByEventoId(eventoId);
        List<Compra> compras = compraRepository.findByEventoId(eventoId);

        Map<UUID, BigDecimal> debitos = new HashMap<>();
        Map<UUID, BigDecimal> creditos = new HashMap<>();
        for (EventoParticipante ep : eps) {
            debitos.put(ep.getParticipanteId(), BigDecimal.ZERO);
            creditos.put(ep.getParticipanteId(), BigDecimal.ZERO);
        }

        for (Compra compra : compras) {
            List<UUID> elegiveis = eps.stream()
                    .filter(ep -> !ep.isMenorDeIdade())
                    .filter(ep -> !ep.excluiCategoria(compra.getCategoriaId()))
                    .map(EventoParticipante::getParticipanteId)
                    .toList();

            if (!elegiveis.isEmpty()) {
                BigDecimal parcela = compra.getValor()
                        .divide(BigDecimal.valueOf(elegiveis.size()), 2, RoundingMode.HALF_UP);
                for (UUID pid : elegiveis) {
                    debitos.merge(pid, parcela, BigDecimal::add);
                }
            }

            List<UUID> pagadores = List.copyOf(compra.getPagadoresIds());
            if (!pagadores.isEmpty()) {
                BigDecimal creditoPorPagador = compra.getValor()
                        .divide(BigDecimal.valueOf(pagadores.size()), 2, RoundingMode.HALF_UP);
                for (UUID pid : pagadores) {
                    creditos.merge(pid, creditoPorPagador, BigDecimal::add);
                }
            }
        }

        return eps.stream().map(ep -> {
            UUID pid = ep.getParticipanteId();
            Participante participante = participanteRepository.findById(pid)
                    .orElseThrow(() -> new ParticipanteNotFoundException(pid));
            BigDecimal devido = debitos.getOrDefault(pid, BigDecimal.ZERO);
            BigDecimal pago = creditos.getOrDefault(pid, BigDecimal.ZERO);
            BigDecimal saldo = pago.subtract(devido);
            return new RateioItem(pid, participante.getNome(), devido, pago, saldo);
        }).toList();
    }
}
