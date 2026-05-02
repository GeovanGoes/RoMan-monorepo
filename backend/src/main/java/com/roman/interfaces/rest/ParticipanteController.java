package com.roman.interfaces.rest;

import com.roman.application.participante.*;
import com.roman.interfaces.dto.request.AtualizarParticipanteRequest;
import com.roman.interfaces.dto.request.CriarParticipanteRequest;
import com.roman.interfaces.dto.response.ParticipanteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/participantes")
public class ParticipanteController {

    private final CriarParticipanteUseCase criarUseCase;
    private final AtualizarParticipanteUseCase atualizarUseCase;
    private final BuscarParticipanteUseCase buscarUseCase;
    private final ListarParticipantesUseCase listarUseCase;
    private final RemoverParticipanteUseCase removerUseCase;

    public ParticipanteController(CriarParticipanteUseCase criarUseCase,
                                  AtualizarParticipanteUseCase atualizarUseCase,
                                  BuscarParticipanteUseCase buscarUseCase,
                                  ListarParticipantesUseCase listarUseCase,
                                  RemoverParticipanteUseCase removerUseCase) {
        this.criarUseCase = criarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.buscarUseCase = buscarUseCase;
        this.listarUseCase = listarUseCase;
        this.removerUseCase = removerUseCase;
    }

    @PostMapping
    public ResponseEntity<ParticipanteResponse> criar(@Valid @RequestBody CriarParticipanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ParticipanteResponse.from(criarUseCase.execute(request.nome(), request.username())));
    }

    @GetMapping
    public List<ParticipanteResponse> listar() {
        return listarUseCase.execute().stream().map(ParticipanteResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ParticipanteResponse buscar(@PathVariable UUID id) {
        return ParticipanteResponse.from(buscarUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public ParticipanteResponse atualizar(@PathVariable UUID id,
                                          @Valid @RequestBody AtualizarParticipanteRequest request) {
        return ParticipanteResponse.from(atualizarUseCase.execute(id, request.nome(), request.username()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        removerUseCase.execute(id);
    }
}
