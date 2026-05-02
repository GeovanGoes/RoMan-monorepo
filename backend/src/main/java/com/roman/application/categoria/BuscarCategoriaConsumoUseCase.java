package com.roman.application.categoria;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarCategoriaConsumoUseCase {

    private final CategoriaConsumoRepository repository;

    public BuscarCategoriaConsumoUseCase(CategoriaConsumoRepository repository) {
        this.repository = repository;
    }

    public CategoriaConsumo execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoriaConsumoNotFoundException(id));
    }
}
