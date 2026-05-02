package com.roman.infrastructure.security;

import com.roman.application.port.JwtTokenService;
import com.roman.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenServiceImpl implements JwtTokenService {

    private final SecretKey signingKey;
    private final long accessTokenExpiration;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtTokenServiceImpl(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration}") long accessTokenExpiration) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
    }

    @Override
    public String gerarAccessToken(Usuario usuario) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration * 1000L);
        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("perfil", usuario.getPerfil().name())
                .claim("senhaProvisoria", usuario.isSenhaProvisoria())
                .claim("nome", usuario.getNome())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String gerarRefreshTokenRaw(UUID usuarioId) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public UUID extrairUsuarioId(String token) {
        Claims claims = Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }

    @Override
    public boolean isAccessTokenValido(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
