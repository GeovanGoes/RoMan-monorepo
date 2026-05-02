package com.roman.application.auth;

import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.CredenciaisInvalidasException;
import com.roman.domain.exception.SenhaFracaException;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AlterarSenhaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;

    public AlterarSenhaUseCase(UsuarioRepository usuarioRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(UUID usuarioId, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        validarForcaSenha(novaSenha);
        usuario.alterarSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        refreshTokenRepository.revokeAllByUsuarioId(usuarioId);
    }

    private void validarForcaSenha(String senha) {
        if (senha == null || senha.length() < 8 || !senha.matches(".*\\d.*")) {
            throw new SenhaFracaException();
        }
    }
}
