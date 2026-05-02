package com.roman.application.participante;

import com.roman.domain.entity.Participante;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarParticipanteUseCase {

    private final ParticipanteRepository repository;

    public BuscarParticipanteUseCase(ParticipanteRepository repository) {
        this.repository = repository;
    }

    public Participante execute(UUID id) {
        return repository.findById(id)
                .filter(Participante::isAtivo)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));
    }
}
