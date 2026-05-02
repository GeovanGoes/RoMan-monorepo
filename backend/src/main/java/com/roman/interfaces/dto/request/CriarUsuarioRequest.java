package com.roman.interfaces.dto.request;

import com.roman.domain.entity.PerfilUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarUsuarioRequest(
    @NotBlank String nome,
    @NotBlank String username,
    @NotBlank @Size(min = 8) String senha,
    String email,
    String telefone,
    @NotNull PerfilUsuario perfil
) {}
