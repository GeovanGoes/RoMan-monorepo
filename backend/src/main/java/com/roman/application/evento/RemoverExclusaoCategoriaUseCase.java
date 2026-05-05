package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.exception.EventoParticipanteNotFoundException;
import com.roman.domain.repository.EventoParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverExclusaoCategoriaUseCase {

    private final EventoParticipanteRepository repository;

    public RemoverExclusaoCategoriaUseCase(EventoParticipanteRepository repository) {
        this.repository = repository;
    }

    public EventoParticipante execute(UUID eventoId, UUID usuarioId, UUID categoriaId) {
        EventoParticipante ep = repository.findByEventoIdAndUsuarioId(eventoId, usuarioId)
                .orElseThrow(() -> new EventoParticipanteNotFoundException(usuarioId, eventoId));

        ep.removerExclusaoCategoria(categoriaId);
        return repository.save(ep);
    }
}
