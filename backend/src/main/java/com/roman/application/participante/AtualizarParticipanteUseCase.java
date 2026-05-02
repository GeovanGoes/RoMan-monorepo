package com.roman.application.participante;

import com.roman.domain.entity.Participante;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.exception.UsernameJaExisteException;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtualizarParticipanteUseCase {

    private final ParticipanteRepository repository;

    public AtualizarParticipanteUseCase(ParticipanteRepository repository) {
        this.repository = repository;
    }

    public Participante execute(UUID id, String nome, String username) {
        Participante participante = repository.findById(id)
                .filter(Participante::isAtivo)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));

        if (repository.existsByUsernameAtivoAndIdNot(username, id)) {
            throw new UsernameJaExisteException(username);
        }

        participante.atualizar(nome, username);
        return repository.save(participante);
    }
}
