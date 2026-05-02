package com.roman.domain.repository;

import com.roman.domain.entity.Compra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompraRepository {
    Compra save(Compra compra);
    Optional<Compra> findById(UUID id);
    List<Compra> findByEventoId(UUID eventoId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
