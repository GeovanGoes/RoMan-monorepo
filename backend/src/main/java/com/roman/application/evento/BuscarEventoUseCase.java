package com.roman.application.evento;

import com.roman.domain.entity.Evento;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarEventoUseCase {

    private final EventoRepository repository;

    public BuscarEventoUseCase(EventoRepository repository) {
        this.repository = repository;
    }

    public Evento execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventoNotFoundException(id));
    }
}
