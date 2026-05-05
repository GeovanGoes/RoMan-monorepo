package com.roman.application.participante;

import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.UsernameJaExisteException;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class CriarParticipanteUseCase {

    private final UsuarioRepository repository;

    public CriarParticipanteUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario execute(String nome, String username) {
        if (repository.existsByUsernameAtivo(username)) {
            throw new UsernameJaExisteException(username);
        }
        return repository.save(Usuario.criarConvidado(nome, username));
    }
}
