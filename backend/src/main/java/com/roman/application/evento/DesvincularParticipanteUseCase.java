package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.exception.EventoParticipanteNotFoundException;
import com.roman.domain.repository.EventoParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DesvincularParticipanteUseCase {

    private final EventoParticipanteRepository repository;

    public DesvincularParticipanteUseCase(EventoParticipanteRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID eventoId, UUID participanteId) {
        EventoParticipante ep = repository.findByEventoIdAndParticipanteId(eventoId, participanteId)
                .orElseThrow(() -> new EventoParticipanteNotFoundException(participanteId, eventoId));
        repository.delete(ep);
    }
}
