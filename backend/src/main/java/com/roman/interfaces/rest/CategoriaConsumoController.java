package com.roman.interfaces.rest;

import com.roman.application.categoria.*;
import com.roman.interfaces.dto.request.AtualizarCategoriaConsumoRequest;
import com.roman.interfaces.dto.request.CriarCategoriaConsumoRequest;
import com.roman.interfaces.dto.response.CategoriaConsumoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaConsumoController {

    private final CriarCategoriaConsumoUseCase criarUseCase;
    private final AtualizarCategoriaConsumoUseCase atualizarUseCase;
    private final BuscarCategoriaConsumoUseCase buscarUseCase;
    private final ListarCategoriasConsumoUseCase listarUseCase;
    private final RemoverCategoriaConsumoUseCase removerUseCase;

    public CategoriaConsumoController(CriarCategoriaConsumoUseCase criarUseCase,
                                      AtualizarCategoriaConsumoUseCase atualizarUseCase,
                                      BuscarCategoriaConsumoUseCase buscarUseCase,
                                      ListarCategoriasConsumoUseCase listarUseCase,
                                      RemoverCategoriaConsumoUseCase removerUseCase) {
        this.criarUseCase = criarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.buscarUseCase = buscarUseCase;
        this.listarUseCase = listarUseCase;
        this.removerUseCase = removerUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaConsumoResponse> criar(@Valid @RequestBody CriarCategoriaConsumoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaConsumoResponse.from(criarUseCase.execute(request.nome(), request.descricao())));
    }

    @GetMapping
    public List<CategoriaConsumoResponse> listar() {
        return listarUseCase.execute().stream().map(CategoriaConsumoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CategoriaConsumoResponse buscar(@PathVariable UUID id) {
        return CategoriaConsumoResponse.from(buscarUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaConsumoResponse atualizar(@PathVariable UUID id,
                                              @Valid @RequestBody AtualizarCategoriaConsumoRequest request) {
        return CategoriaConsumoResponse.from(atualizarUseCase.execute(id, request.nome(), request.descricao()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        removerUseCase.execute(id);
    }
}
