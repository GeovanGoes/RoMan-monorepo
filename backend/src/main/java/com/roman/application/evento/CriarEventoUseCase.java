package com.roman.application.evento;

import com.roman.domain.entity.Evento;
import com.roman.domain.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CriarEventoUseCase {

    private final EventoRepository repository;

    public CriarEventoUseCase(EventoRepository repository) {
        this.repository = repository;
    }

    public Evento execute(String nome, String local, LocalDate dataInicio, LocalDate dataFim) {
        return repository.save(Evento.criar(nome, local, dataInicio, dataFim));
    }
}
