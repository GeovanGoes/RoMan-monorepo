package com.roman.application.categoria;

import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverCategoriaConsumoUseCase {

    private final CategoriaConsumoRepository repository;

    public RemoverCategoriaConsumoUseCase(CategoriaConsumoRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        if (!repository.existsById(id)) {
            throw new CategoriaConsumoNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
