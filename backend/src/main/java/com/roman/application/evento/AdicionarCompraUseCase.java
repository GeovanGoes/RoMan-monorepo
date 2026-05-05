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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Service
public class AdicionarCompraUseCase {

    private final EventoRepository eventoRepository;
    private final CategoriaConsumoRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;

    public AdicionarCompraUseCase(EventoRepository eventoRepository,
                                  CategoriaConsumoRepository categoriaRepository,
                                  UsuarioRepository usuarioRepository,
                                  CompraRepository compraRepository) {
        this.eventoRepository = eventoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
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
            usuarioRepository.findById(pagadorId)
                    .filter(Usuario::isAtivo)
                    .orElseThrow(() -> new ParticipanteNotFoundException(pagadorId));
        }
        return compraRepository.save(Compra.criar(descricao, valor, eventoId, categoriaId, pagadoresIds));
    }
}
