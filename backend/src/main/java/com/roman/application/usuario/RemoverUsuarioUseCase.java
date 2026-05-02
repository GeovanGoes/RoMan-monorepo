package com.roman.application.usuario;

import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RemoverUsuarioUseCase(UsuarioRepository usuarioRepository,
                                 RefreshTokenRepository refreshTokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(UUID id) {
        Usuario usuario = usuarioRepository.findById(id).filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
        usuario.remover();
        usuarioRepository.save(usuario);
        refreshTokenRepository.revokeAllByUsuarioId(id);
    }
}
