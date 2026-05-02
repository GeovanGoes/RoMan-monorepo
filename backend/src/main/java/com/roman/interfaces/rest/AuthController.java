package com.roman.interfaces.rest;

import com.roman.application.auth.AlterarSenhaUseCase;
import com.roman.application.auth.LoginUseCase;
import com.roman.application.auth.LogoutUseCase;
import com.roman.application.auth.RedefinirSenhaUseCase;
import com.roman.application.auth.RefreshTokenUseCase;
import com.roman.application.auth.SolicitarRecuperacaoSenhaUseCase;
import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import com.roman.interfaces.dto.request.AlterarSenhaRequest;
import com.roman.interfaces.dto.request.LoginRequest;
import com.roman.interfaces.dto.request.LogoutRequest;
import com.roman.interfaces.dto.request.RecuperarSenhaRequest;
import com.roman.interfaces.dto.request.RedefinirSenhaRequest;
import com.roman.interfaces.dto.request.RefreshRequest;
import com.roman.interfaces.dto.response.LoginResponse;
import com.roman.interfaces.dto.response.RefreshResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AlterarSenhaUseCase alterarSenhaUseCase;
    private final SolicitarRecuperacaoSenhaUseCase recuperacaoSenhaUseCase;
    private final RedefinirSenhaUseCase redefinirSenhaUseCase;
    private final UsuarioRepository usuarioRepository;

    public AuthController(LoginUseCase loginUseCase, RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase, AlterarSenhaUseCase alterarSenhaUseCase,
                          SolicitarRecuperacaoSenhaUseCase recuperacaoSenhaUseCase,
                          RedefinirSenhaUseCase redefinirSenhaUseCase,
                          UsuarioRepository usuarioRepository) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.alterarSenhaUseCase = alterarSenhaUseCase;
        this.recuperacaoSenhaUseCase = recuperacaoSenhaUseCase;
        this.redefinirSenhaUseCase = redefinirSenhaUseCase;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        LoginUseCase.LoginResult result = loginUseCase.execute(request.username(), request.senha());
        return new LoginResponse(result.accessToken(), result.refreshTokenRaw(),
                result.usuario().getPerfil(), result.usuario().isSenhaProvisoria(), result.usuario().getNome());
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(@Valid @RequestBody RefreshRequest request) {
        String newAccessToken = refreshTokenUseCase.execute(request.refreshToken());
        return new RefreshResponse(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        logoutUseCase.execute(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequest request) {
        recuperacaoSenhaUseCase.execute(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        redefinirSenhaUseCase.execute(request.token(), request.novaSenha());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        alterarSenhaUseCase.execute(usuario.getId(), request.senhaAtual(), request.novaSenha());
        return ResponseEntity.noContent().build();
    }
}
