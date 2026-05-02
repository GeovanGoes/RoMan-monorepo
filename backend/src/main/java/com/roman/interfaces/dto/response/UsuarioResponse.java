package com.roman.interfaces.dto.response;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(UUID id, String nome, String username, String email,
                               String telefone, PerfilUsuario perfil, boolean senhaProvisoria,
                               LocalDateTime createdAt) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getUsername(), u.getEmail(),
                u.getTelefone(), u.getPerfil(), u.isSenhaProvisoria(), u.getCreatedAt());
    }
}
