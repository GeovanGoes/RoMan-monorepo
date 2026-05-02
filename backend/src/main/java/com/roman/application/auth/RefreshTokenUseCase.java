package com.roman.application.auth;

import com.roman.domain.entity.RefreshToken;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.TokenInvalidoException;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import com.roman.application.port.JwtTokenService;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtTokenService jwtTokenService;

    public RefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
                               UsuarioRepository usuarioRepository,
                               JwtTokenService jwtTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public String execute(String refreshTokenRaw) {
        String hash = LoginUseCase.sha256(refreshTokenRaw);
        RefreshToken rt = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(TokenInvalidoException::new);

        if (!rt.isValido()) throw new TokenInvalidoException();

        Usuario usuario = usuarioRepository.findById(rt.getUsuarioId())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(rt.getUsuarioId()));

        return jwtTokenService.gerarAccessToken(usuario);
    }
}
