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

    public EventoParticipante execute(UUID eventoId, UUID participanteId, UUID categoriaId) {
        EventoParticipante ep = repository.findByEventoIdAndParticipanteId(eventoId, participanteId)
                .orElseThrow(() -> new EventoParticipanteNotFoundException(participanteId, eventoId));

        ep.removerExclusaoCategoria(categoriaId);
        return repository.save(ep);
    }
}
