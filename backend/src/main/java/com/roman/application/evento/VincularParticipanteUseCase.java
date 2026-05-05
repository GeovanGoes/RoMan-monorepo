package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.ParticipanteJaVinculadoException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VincularParticipanteUseCase {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoParticipanteRepository eventoParticipanteRepository;

    public VincularParticipanteUseCase(EventoRepository eventoRepository,
                                       UsuarioRepository usuarioRepository,
                                       EventoParticipanteRepository eventoParticipanteRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoParticipanteRepository = eventoParticipanteRepository;
    }

    public EventoParticipante execute(UUID eventoId, UUID usuarioId, boolean menorDeIdade) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }

        usuarioRepository.findById(usuarioId)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ParticipanteNotFoundException(usuarioId));

        if (eventoParticipanteRepository.existsByEventoIdAndUsuarioId(eventoId, usuarioId)) {
            throw new ParticipanteJaVinculadoException(usuarioId, eventoId);
        }

        return eventoParticipanteRepository.save(
                EventoParticipante.criar(eventoId, usuarioId, menorDeIdade));
    }
}
