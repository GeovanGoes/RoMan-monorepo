package com.roman.interfaces.rest;

import com.roman.application.usuario.AtualizarUsuarioUseCase;
import com.roman.application.usuario.BuscarUsuarioUseCase;
import com.roman.application.usuario.CriarUsuarioUseCase;
import com.roman.application.usuario.ListarUsuariosUseCase;
import com.roman.application.usuario.RemoverUsuarioUseCase;
import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import com.roman.interfaces.dto.request.AtualizarUsuarioRequest;
import com.roman.interfaces.dto.request.CriarUsuarioRequest;
import com.roman.interfaces.dto.response.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final CriarUsuarioUseCase criarUseCase;
    private final AtualizarUsuarioUseCase atualizarUseCase;
    private final BuscarUsuarioUseCase buscarUseCase;
    private final ListarUsuariosUseCase listarUseCase;
    private final RemoverUsuarioUseCase removerUseCase;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(CriarUsuarioUseCase criarUseCase, AtualizarUsuarioUseCase atualizarUseCase,
                             BuscarUsuarioUseCase buscarUseCase, ListarUsuariosUseCase listarUseCase,
                             RemoverUsuarioUseCase removerUseCase, UsuarioRepository usuarioRepository) {
        this.criarUseCase = criarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.buscarUseCase = buscarUseCase;
        this.listarUseCase = listarUseCase;
        this.removerUseCase = removerUseCase;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CriarUsuarioRequest request) {
        Usuario usuario = criarUseCase.execute(request.nome(), request.username(), request.email(),
                request.telefone(), request.senha(), request.perfil());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> listar() {
        return listarUseCase.execute().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse buscar(@PathVariable UUID id) {
        return UsuarioResponse.from(buscarUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse atualizar(@PathVariable UUID id, @Valid @RequestBody AtualizarUsuarioRequest request) {
        return UsuarioResponse.from(atualizarUseCase.execute(id, request.nome(), request.email(), request.telefone()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        removerUseCase.execute(id);
    }

    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return UsuarioResponse.from(usuario);
    }
}
