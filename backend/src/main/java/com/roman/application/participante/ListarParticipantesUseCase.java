package com.roman.application.participante;

import com.roman.domain.entity.Participante;
import com.roman.domain.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarParticipantesUseCase {

    private final ParticipanteRepository repository;

    public ListarParticipantesUseCase(ParticipanteRepository repository) {
        this.repository = repository;
    }

    public List<Participante> execute() {
        return repository.findAllAtivos();
    }
}
