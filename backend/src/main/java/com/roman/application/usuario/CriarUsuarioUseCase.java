package com.roman.application.usuario;

import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.AdminSemEmailException;
import com.roman.domain.exception.EmailJaExisteException;
import com.roman.domain.exception.UsernameJaExisteParaUsuarioException;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class CriarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    public CriarUsuarioUseCase(UsuarioRepository usuarioRepository,
                               PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario execute(String nome, String username, String email, String telefone,
                           String senhaRaw, PerfilUsuario perfil) {
        if (perfil == PerfilUsuario.ADMIN && (email == null || email.isBlank())) {
            throw new AdminSemEmailException();
        }
        if (usuarioRepository.existsByUsername(username)) {
            throw new UsernameJaExisteParaUsuarioException(username);
        }
        if (email != null && !email.isBlank()) {
            usuarioRepository.findByEmail(email).ifPresent(u -> { throw new EmailJaExisteException(); });
        }
        String senhaHash = passwordEncoder.encode(senhaRaw);
        Usuario usuario = Usuario.criar(nome, username, email, telefone, senhaHash, perfil);
        return usuarioRepository.save(usuario);
    }
}
