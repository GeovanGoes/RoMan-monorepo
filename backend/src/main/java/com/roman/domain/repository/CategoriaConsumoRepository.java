package com.roman.domain.repository;

import com.roman.domain.entity.CategoriaConsumo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaConsumoRepository {
    CategoriaConsumo save(CategoriaConsumo categoria);
    Optional<CategoriaConsumo> findById(UUID id);
    List<CategoriaConsumo> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
