package com.roman.application.evento;

import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverEventoUseCase {

    private final EventoRepository repository;

    public RemoverEventoUseCase(EventoRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        if (!repository.existsById(id)) {
            throw new EventoNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
