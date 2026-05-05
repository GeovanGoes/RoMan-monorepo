package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.exception.EventoParticipanteNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import com.roman.domain.repository.EventoParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdicionarExclusaoCategoriaUseCase {

    private final EventoParticipanteRepository epRepository;
    private final CategoriaConsumoRepository categoriaRepository;

    public AdicionarExclusaoCategoriaUseCase(EventoParticipanteRepository epRepository,
                                             CategoriaConsumoRepository categoriaRepository) {
        this.epRepository = epRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public EventoParticipante execute(UUID eventoId, UUID usuarioId, UUID categoriaId) {
        EventoParticipante ep = epRepository.findByEventoIdAndUsuarioId(eventoId, usuarioId)
                .orElseThrow(() -> new EventoParticipanteNotFoundException(usuarioId, eventoId));

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new CategoriaConsumoNotFoundException(categoriaId);
        }

        ep.adicionarExclusaoCategoria(categoriaId);
        return epRepository.save(ep);
    }
}
