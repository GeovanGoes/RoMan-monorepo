package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarParticipantesDoEventoUseCase {

    private final EventoRepository eventoRepository;
    private final EventoParticipanteRepository epRepository;

    public ListarParticipantesDoEventoUseCase(EventoRepository eventoRepository,
                                              EventoParticipanteRepository epRepository) {
        this.eventoRepository = eventoRepository;
        this.epRepository = epRepository;
    }

    public List<EventoParticipante> execute(UUID eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }
        return epRepository.findByEventoId(eventoId);
    }
}
