package com.roman.interfaces.dto.response;

import com.roman.domain.entity.PerfilUsuario;

public record LoginResponse(String accessToken, String refreshToken, PerfilUsuario perfil,
                             boolean senhaProvisoria, String nome) {}
