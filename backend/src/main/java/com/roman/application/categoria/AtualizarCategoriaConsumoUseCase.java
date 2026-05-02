package com.roman.application.categoria;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtualizarCategoriaConsumoUseCase {

    private final CategoriaConsumoRepository repository;

    public AtualizarCategoriaConsumoUseCase(CategoriaConsumoRepository repository) {
        this.repository = repository;
    }

    public CategoriaConsumo execute(UUID id, String nome, String descricao) {
        CategoriaConsumo categoria = repository.findById(id)
                .orElseThrow(() -> new CategoriaConsumoNotFoundException(id));
        categoria.atualizar(nome, descricao);
        return repository.save(categoria);
    }
}
