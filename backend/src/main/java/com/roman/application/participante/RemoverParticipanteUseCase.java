package com.roman.application.participante;

import com.roman.domain.entity.Participante;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverParticipanteUseCase {

    private final ParticipanteRepository repository;

    public RemoverParticipanteUseCase(ParticipanteRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        Participante participante = repository.findById(id)
                .filter(Participante::isAtivo)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));

        participante.remover();
        repository.save(participante);
    }
}
