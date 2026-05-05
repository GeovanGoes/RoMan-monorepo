package com.roman.application.evento;

import com.roman.domain.entity.Compra;
import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.CompraRepository;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public CalcularRateioUseCase(EventoRepository eventoRepository,
                                 EventoParticipanteRepository eventoParticipanteRepository,
                                 CompraRepository compraRepository,
                                 UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.eventoParticipanteRepository = eventoParticipanteRepository;
        this.compraRepository = compraRepository;
        this.usuarioRepository = usuarioRepository;
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
            debitos.put(ep.getUsuarioId(), BigDecimal.ZERO);
            creditos.put(ep.getUsuarioId(), BigDecimal.ZERO);
        }

        for (Compra compra : compras) {
            List<UUID> elegiveis = eps.stream()
                    .filter(ep -> !ep.isMenorDeIdade())
                    .filter(ep -> !ep.excluiCategoria(compra.getCategoriaId()))
                    .map(EventoParticipante::getUsuarioId)
                    .toList();

            if (!elegiveis.isEmpty()) {
                BigDecimal parcela = compra.getValor()
                        .divide(BigDecimal.valueOf(elegiveis.size()), 2, RoundingMode.HALF_UP);
                for (UUID uid : elegiveis) {
                    debitos.merge(uid, parcela, BigDecimal::add);
                }
            }

            List<UUID> pagadores = List.copyOf(compra.getPagadoresIds());
            if (!pagadores.isEmpty()) {
                BigDecimal creditoPorPagador = compra.getValor()
                        .divide(BigDecimal.valueOf(pagadores.size()), 2, RoundingMode.HALF_UP);
                for (UUID uid : pagadores) {
                    creditos.merge(uid, creditoPorPagador, BigDecimal::add);
                }
            }
        }

        return eps.stream().map(ep -> {
            UUID uid = ep.getUsuarioId();
            Usuario usuario = usuarioRepository.findById(uid)
                    .orElseThrow(() -> new ParticipanteNotFoundException(uid));
            BigDecimal devido = debitos.getOrDefault(uid, BigDecimal.ZERO);
            BigDecimal pago = creditos.getOrDefault(uid, BigDecimal.ZERO);
            BigDecimal saldo = pago.subtract(devido);
            return new RateioItem(uid, usuario.getNome(), devido, pago, saldo);
        }).toList();
    }
}
