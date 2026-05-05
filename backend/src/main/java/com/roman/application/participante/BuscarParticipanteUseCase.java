package com.roman.application.participante;

import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarParticipanteUseCase {

    private final UsuarioRepository repository;

    public BuscarParticipanteUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario execute(UUID id) {
        return repository.findById(id)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));
    }
}
