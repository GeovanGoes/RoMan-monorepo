package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.entity.Participante;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.ParticipanteJaVinculadoException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VincularParticipanteUseCase {

    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;
    private final EventoParticipanteRepository eventoParticipanteRepository;

    public VincularParticipanteUseCase(EventoRepository eventoRepository,
                                       ParticipanteRepository participanteRepository,
                                       EventoParticipanteRepository eventoParticipanteRepository) {
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.eventoParticipanteRepository = eventoParticipanteRepository;
    }

    public EventoParticipante execute(UUID eventoId, UUID participanteId, boolean menorDeIdade) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }

        Participante participante = participanteRepository.findById(participanteId)
                .filter(Participante::isAtivo)
                .orElseThrow(() -> new ParticipanteNotFoundException(participanteId));

        if (eventoParticipanteRepository.existsByEventoIdAndParticipanteId(eventoId, participanteId)) {
            throw new ParticipanteJaVinculadoException(participanteId, eventoId);
        }

        return eventoParticipanteRepository.save(
                EventoParticipante.criar(eventoId, participanteId, menorDeIdade));
    }
}
