package com.roman.application.auth;

import com.roman.application.port.JwtTokenService;
import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.RefreshToken;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.CredenciaisInvalidasException;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final long refreshTokenExpiration;

    public record LoginResult(Usuario usuario, String accessToken, String refreshTokenRaw) {}

    public LoginUseCase(UsuarioRepository usuarioRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoderPort passwordEncoder,
                        JwtTokenService jwtTokenService,
                        @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public LoginResult execute(String username, String senhaRaw) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .filter(Usuario::isAtivo)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(senhaRaw, usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        String accessToken = jwtTokenService.gerarAccessToken(usuario);
        String refreshTokenRaw = jwtTokenService.gerarRefreshTokenRaw(usuario.getId());
        String refreshTokenHash = sha256(refreshTokenRaw);
        RefreshToken rt = RefreshToken.criar(usuario.getId(), refreshTokenHash, refreshTokenExpiration);
        refreshTokenRepository.save(rt);

        return new LoginResult(usuario, accessToken, refreshTokenRaw);
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
