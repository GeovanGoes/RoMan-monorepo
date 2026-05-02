package com.roman.application.categoria;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.springframework.stereotype.Service;

@Service
public class CriarCategoriaConsumoUseCase {

    private final CategoriaConsumoRepository repository;

    public CriarCategoriaConsumoUseCase(CategoriaConsumoRepository repository) {
        this.repository = repository;
    }

    public CategoriaConsumo execute(String nome, String descricao) {
        return repository.save(CategoriaConsumo.criar(nome, descricao));
    }
}
