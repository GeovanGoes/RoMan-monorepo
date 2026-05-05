package com.roman.application.participante;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverParticipanteUseCase {

    private final UsuarioRepository repository;

    public RemoverParticipanteUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        Usuario usuario = repository.findById(id)
                .filter(u -> u.isAtivo() && u.getPerfil() == PerfilUsuario.CONVIDADO)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));

        usuario.remover();
        repository.save(usuario);
    }
}
