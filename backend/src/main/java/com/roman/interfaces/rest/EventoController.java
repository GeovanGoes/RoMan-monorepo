package com.roman.interfaces.rest;

import com.roman.application.evento.*;
import com.roman.interfaces.dto.request.AdicionarCompraRequest;
import com.roman.interfaces.dto.request.AtualizarEventoRequest;
import com.roman.interfaces.dto.request.CriarEventoRequest;
import com.roman.interfaces.dto.request.VincularParticipanteRequest;
import com.roman.interfaces.dto.response.CompraResponse;
import com.roman.interfaces.dto.response.EventoParticipanteResponse;
import com.roman.interfaces.dto.response.EventoResponse;
import com.roman.interfaces.dto.response.RateioItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/eventos")
public class EventoController {

    private final CriarEventoUseCase criarUseCase;
    private final AtualizarEventoUseCase atualizarUseCase;
    private final BuscarEventoUseCase buscarUseCase;
    private final ListarEventosUseCase listarUseCase;
    private final RemoverEventoUseCase removerUseCase;
    private final VincularParticipanteUseCase vincularUseCase;
    private final DesvincularParticipanteUseCase desvincularUseCase;
    private final ListarParticipantesDoEventoUseCase listarParticipantesUseCase;
    private final AdicionarExclusaoCategoriaUseCase adicionarExclusaoUseCase;
    private final RemoverExclusaoCategoriaUseCase removerExclusaoUseCase;
    private final AdicionarCompraUseCase adicionarCompraUseCase;
    private final ListarComprasUseCase listarComprasUseCase;
    private final RemoverCompraUseCase removerCompraUseCase;
    private final CalcularRateioUseCase calcularRateioUseCase;

    public EventoController(CriarEventoUseCase criarUseCase,
                            AtualizarEventoUseCase atualizarUseCase,
                            BuscarEventoUseCase buscarUseCase,
                            ListarEventosUseCase listarUseCase,
                            RemoverEventoUseCase removerUseCase,
                            VincularParticipanteUseCase vincularUseCase,
                            DesvincularParticipanteUseCase desvincularUseCase,
                            ListarParticipantesDoEventoUseCase listarParticipantesUseCase,
                            AdicionarExclusaoCategoriaUseCase adicionarExclusaoUseCase,
                            RemoverExclusaoCategoriaUseCase removerExclusaoUseCase,
                            AdicionarCompraUseCase adicionarCompraUseCase,
                            ListarComprasUseCase listarComprasUseCase,
                            RemoverCompraUseCase removerCompraUseCase,
                            CalcularRateioUseCase calcularRateioUseCase) {
        this.criarUseCase = criarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.buscarUseCase = buscarUseCase;
        this.listarUseCase = listarUseCase;
        this.removerUseCase = removerUseCase;
        this.vincularUseCase = vincularUseCase;
        this.desvincularUseCase = desvincularUseCase;
        this.listarParticipantesUseCase = listarParticipantesUseCase;
        this.adicionarExclusaoUseCase = adicionarExclusaoUseCase;
        this.removerExclusaoUseCase = removerExclusaoUseCase;
        this.adicionarCompraUseCase = adicionarCompraUseCase;
        this.listarComprasUseCase = listarComprasUseCase;
        this.removerCompraUseCase = removerCompraUseCase;
        this.calcularRateioUseCase = calcularRateioUseCase;
    }

    // --- Evento CRUD ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> criar(@Valid @RequestBody CriarEventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EventoResponse.from(criarUseCase.execute(
                        request.nome(), request.local(), request.dataInicio(), request.dataFim())));
    }

    @GetMapping
    public List<EventoResponse> listar() {
        return listarUseCase.execute().stream().map(EventoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable UUID id) {
        return EventoResponse.from(buscarUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EventoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody AtualizarEventoRequest request) {
        return EventoResponse.from(atualizarUseCase.execute(
                id, request.nome(), request.local(), request.dataInicio(), request.dataFim()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        removerUseCase.execute(id);
    }

    // --- Participantes no evento ---

    @GetMapping("/{id}/participantes")
    public List<EventoParticipanteResponse> listarParticipantes(@PathVariable UUID id) {
        return listarParticipantesUseCase.execute(id).stream().map(EventoParticipanteResponse::from).toList();
    }

    @PostMapping("/{id}/participantes/{participanteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoParticipanteResponse> vincularParticipante(
            @PathVariable UUID id,
            @PathVariable UUID participanteId,
            @RequestBody(required = false) VincularParticipanteRequest request) {
        boolean menor = request != null && request.menorDeIdade();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EventoParticipanteResponse.from(vincularUseCase.execute(id, participanteId, menor)));
    }

    @DeleteMapping("/{id}/participantes/{participanteId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desvincularParticipante(@PathVariable UUID id, @PathVariable UUID participanteId) {
        desvincularUseCase.execute(id, participanteId);
    }

    // --- Exclusões de categoria ---

    @PostMapping("/{id}/participantes/{participanteId}/exclusoes/{categoriaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoParticipanteResponse> adicionarExclusao(
            @PathVariable UUID id,
            @PathVariable UUID participanteId,
            @PathVariable UUID categoriaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EventoParticipanteResponse.from(adicionarExclusaoUseCase.execute(id, participanteId, categoriaId)));
    }

    @DeleteMapping("/{id}/participantes/{participanteId}/exclusoes/{categoriaId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerExclusao(@PathVariable UUID id, @PathVariable UUID participanteId, @PathVariable UUID categoriaId) {
        removerExclusaoUseCase.execute(id, participanteId, categoriaId);
    }

    // --- Compras ---

    @PostMapping("/{id}/compras")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompraResponse> adicionarCompra(@PathVariable UUID id,
                                                          @Valid @RequestBody AdicionarCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CompraResponse.from(adicionarCompraUseCase.execute(
                        request.descricao(), request.valor(), id, request.categoriaId(), request.pagadoresIds())));
    }

    @GetMapping("/{id}/compras")
    public List<CompraResponse> listarCompras(@PathVariable UUID id) {
        return listarComprasUseCase.execute(id).stream().map(CompraResponse::from).toList();
    }

    @DeleteMapping("/{id}/compras/{compraId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerCompra(@PathVariable UUID id, @PathVariable UUID compraId) {
        removerCompraUseCase.execute(compraId);
    }

    // --- Rateio ---

    @GetMapping("/{id}/rateio")
    public List<RateioItemResponse> calcularRateio(@PathVariable UUID id) {
        return calcularRateioUseCase.execute(id).stream().map(RateioItemResponse::from).toList();
    }
}
