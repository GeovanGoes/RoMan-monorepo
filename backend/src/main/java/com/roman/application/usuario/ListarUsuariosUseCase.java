package com.roman.application.usuario;

import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarUsuariosUseCase {
    private final UsuarioRepository usuarioRepository;
    public ListarUsuariosUseCase(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }

    public List<Usuario> execute() { return usuarioRepository.findAllAtivos(); }
}
