package com.roman.application.usuario;

import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    public BuscarUsuarioUseCase(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }

    public Usuario execute(UUID id) {
        return usuarioRepository.findById(id).filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }
}
