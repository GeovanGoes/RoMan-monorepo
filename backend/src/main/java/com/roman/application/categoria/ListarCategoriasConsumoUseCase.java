package com.roman.application.categoria;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarCategoriasConsumoUseCase {

    private final CategoriaConsumoRepository repository;

    public ListarCategoriasConsumoUseCase(CategoriaConsumoRepository repository) {
        this.repository = repository;
    }

    public List<CategoriaConsumo> execute() {
        return repository.findAll();
    }
}
