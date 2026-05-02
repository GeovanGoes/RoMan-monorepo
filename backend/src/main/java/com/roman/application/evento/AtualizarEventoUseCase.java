package com.roman.application.evento;

import com.roman.domain.entity.Evento;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AtualizarEventoUseCase {

    private final EventoRepository repository;

    public AtualizarEventoUseCase(EventoRepository repository) {
        this.repository = repository;
    }

    public Evento execute(UUID id, String nome, String local, LocalDate dataInicio, LocalDate dataFim) {
        Evento evento = repository.findById(id)
                .orElseThrow(() -> new EventoNotFoundException(id));
        evento.atualizar(nome, local, dataInicio, dataFim);
        return repository.save(evento);
    }
}
