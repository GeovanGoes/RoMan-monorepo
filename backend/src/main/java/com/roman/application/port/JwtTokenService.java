package com.roman.application.port;

import com.roman.domain.entity.Usuario;
import java.util.UUID;

public interface JwtTokenService {
    String gerarAccessToken(Usuario usuario);
    String gerarRefreshTokenRaw(UUID usuarioId);
    UUID extrairUsuarioId(String token);
    boolean isAccessTokenValido(String token);
}
