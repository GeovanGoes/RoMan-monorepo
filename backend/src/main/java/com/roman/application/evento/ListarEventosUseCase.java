package com.roman.application.evento;

import com.roman.domain.entity.Evento;
import com.roman.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarEventosUseCase {

    private final EventoRepository repository;

    public ListarEventosUseCase(EventoRepository repository) {
        this.repository = repository;
    }

    public List<Evento> execute() {
        return repository.findAll();
    }
}
