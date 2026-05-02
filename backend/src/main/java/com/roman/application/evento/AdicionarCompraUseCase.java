package com.roman.application.evento;

import com.roman.domain.entity.Compra;
import com.roman.domain.entity.Participante;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import com.roman.domain.repository.CompraRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Service
public class AdicionarCompraUseCase {

    private final EventoRepository eventoRepository;
    private final CategoriaConsumoRepository categoriaRepository;
    private final ParticipanteRepository participanteRepository;
    private final CompraRepository compraRepository;

    public AdicionarCompraUseCase(EventoRepository eventoRepository,
                                  CategoriaConsumoRepository categoriaRepository,
                                  ParticipanteRepository participanteRepository,
                                  CompraRepository compraRepository) {
        this.eventoRepository = eventoRepository;
        this.categoriaRepository = categoriaRepository;
        this.participanteRepository = participanteRepository;
        this.compraRepository = compraRepository;
    }

    public Compra execute(String descricao, BigDecimal valor, UUID eventoId,
                          UUID categoriaId, Set<UUID> pagadoresIds) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new CategoriaConsumoNotFoundException(categoriaId);
        }
        for (UUID pagadorId : pagadoresIds) {
            participanteRepository.findById(pagadorId)
                    .filter(Participante::isAtivo)
                    .orElseThrow(() -> new ParticipanteNotFoundException(pagadorId));
        }
        return compraRepository.save(Compra.criar(descricao, valor, eventoId, categoriaId, pagadoresIds));
    }
}
