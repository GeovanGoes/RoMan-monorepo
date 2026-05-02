package com.roman.application.evento;

import com.roman.domain.entity.Compra;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.CompraRepository;
import com.roman.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarComprasUseCase {

    private final EventoRepository eventoRepository;
    private final CompraRepository compraRepository;

    public ListarComprasUseCase(EventoRepository eventoRepository, CompraRepository compraRepository) {
        this.eventoRepository = eventoRepository;
        this.compraRepository = compraRepository;
    }

    public List<Compra> execute(UUID eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }
        return compraRepository.findByEventoId(eventoId);
    }
}
