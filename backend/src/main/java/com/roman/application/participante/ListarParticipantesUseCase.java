package com.roman.application.participante;

import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarParticipantesUseCase {

    private final UsuarioRepository repository;

    public ListarParticipantesUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> execute() {
        return repository.findAllAtivos();
    }
}
