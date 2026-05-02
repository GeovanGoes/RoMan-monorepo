package com.roman.application.evento;

import com.roman.domain.exception.CompraNotFoundException;
import com.roman.domain.repository.CompraRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverCompraUseCase {

    private final CompraRepository repository;

    public RemoverCompraUseCase(CompraRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        if (!repository.existsById(id)) {
            throw new CompraNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
