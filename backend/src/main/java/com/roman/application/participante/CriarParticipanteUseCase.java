package com.roman.application.participante;

import com.roman.domain.entity.Participante;
import com.roman.domain.exception.UsernameJaExisteException;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

@Service
public class CriarParticipanteUseCase {

    private final ParticipanteRepository repository;

    public CriarParticipanteUseCase(ParticipanteRepository repository) {
        this.repository = repository;
    }

    public Participante execute(String nome, String username) {
        if (repository.existsByUsernameAtivo(username)) {
            throw new UsernameJaExisteException(username);
        }
        return repository.save(Participante.criar(nome, username));
    }
}
