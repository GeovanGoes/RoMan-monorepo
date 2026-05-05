package com.roman.application.participante;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.exception.UsernameJaExisteException;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AtualizarParticipanteUseCase {

    private final UsuarioRepository repository;

    public AtualizarParticipanteUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario execute(UUID id, String nome, String username) {
        Usuario usuario = repository.findById(id)
                .filter(u -> u.isAtivo() && u.getPerfil() == PerfilUsuario.CONVIDADO)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));

        if (repository.existsByUsernameAtivoAndIdNot(username, id)) {
            throw new UsernameJaExisteException(username);
        }

        usuario.atualizarConvidado(nome, username);
        return repository.save(usuario);
    }
}
