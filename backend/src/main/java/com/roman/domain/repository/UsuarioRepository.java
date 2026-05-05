package com.roman.domain.repository;

import com.roman.domain.entity.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(UUID id);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, UUID id);
    boolean existsByUsernameAtivo(String username);
    boolean existsByUsernameAtivoAndIdNot(String username, UUID id);
    List<Usuario> findAllAtivos();
    List<Usuario> findAllConvidados();
    List<Usuario> findAllByIds(List<UUID> ids);
}
