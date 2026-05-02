package com.roman.domain.repository;

import com.roman.domain.entity.Evento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoRepository {
    Evento save(Evento evento);
    Optional<Evento> findById(UUID id);
    List<Evento> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
