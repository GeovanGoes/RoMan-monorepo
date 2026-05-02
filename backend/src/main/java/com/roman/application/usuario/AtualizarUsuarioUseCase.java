package com.roman.application.usuario;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.AdminSemEmailException;
import com.roman.domain.exception.EmailJaExisteException;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtualizarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public AtualizarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario execute(UUID id, String nome, String email, String telefone) {
        Usuario usuario = usuarioRepository.findById(id)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        if (usuario.getPerfil() == PerfilUsuario.ADMIN && (email == null || email.isBlank())) {
            throw new AdminSemEmailException();
        }
        if (email != null && !email.isBlank()) {
            usuarioRepository.findByEmail(email)
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> { throw new EmailJaExisteException(); });
        }

        usuario.atualizar(nome, email, telefone);
        return usuarioRepository.save(usuario);
    }
}
