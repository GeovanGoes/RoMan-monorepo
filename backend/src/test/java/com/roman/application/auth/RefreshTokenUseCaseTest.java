package com.roman.application.auth;

import com.roman.application.port.JwtTokenService;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.RefreshToken;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.TokenInvalidoException;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private RefreshTokenUseCase useCase;

    private Usuario criarUsuarioAtivo(UUID usuarioId) {
        return new Usuario(usuarioId, "João Silva", "joao123", "joao@email.com",
                "11999999999", "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    private RefreshToken criarRefreshTokenValido(UUID usuarioId, String tokenRaw) {
        return new RefreshToken(UUID.randomUUID(), usuarioId,
                LoginUseCase.sha256(tokenRaw),
                LocalDateTime.now().plusDays(7), false, LocalDateTime.now());
    }

    @Test
    void deve_renovar_access_token_com_sucesso() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "refresh-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        RefreshToken rt = criarRefreshTokenValido(usuarioId, tokenRaw);
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(rt));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(jwtTokenService.gerarAccessToken(usuario)).thenReturn("novo-access-token");

        String resultado = useCase.execute(tokenRaw);

        assertThat(resultado).isEqualTo("novo-access-token");
        verify(jwtTokenService).gerarAccessToken(usuario);
    }

    @Test
    void deve_lancar_excecao_quando_refresh_token_nao_encontrado() {
        String tokenRaw = "token-inexistente";
        String tokenHash = LoginUseCase.sha256(tokenRaw);

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(tokenRaw))
                .isInstanceOf(TokenInvalidoException.class);

        verify(jwtTokenService, never()).gerarAccessToken(any());
    }

    @Test
    void deve_lancar_excecao_quando_refresh_token_revogado() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "refresh-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        RefreshToken rtRevogado = new RefreshToken(UUID.randomUUID(), usuarioId,
                tokenHash, LocalDateTime.now().plusDays(7), true, LocalDateTime.now());

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(rtRevogado));

        assertThatThrownBy(() -> useCase.execute(tokenRaw))
                .isInstanceOf(TokenInvalidoException.class);

        verify(jwtTokenService, never()).gerarAccessToken(any());
    }

    @Test
    void deve_lancar_excecao_quando_refresh_token_expirado() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "refresh-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        RefreshToken rtExpirado = new RefreshToken(UUID.randomUUID(), usuarioId,
                tokenHash, LocalDateTime.now().minusDays(1), false, LocalDateTime.now());

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(rtExpirado));

        assertThatThrownBy(() -> useCase.execute(tokenRaw))
                .isInstanceOf(TokenInvalidoException.class);

        verify(jwtTokenService, never()).gerarAccessToken(any());
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "refresh-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        RefreshToken rt = criarRefreshTokenValido(usuarioId, tokenRaw);

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(rt));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(tokenRaw))
                .isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_inativo() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "refresh-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        RefreshToken rt = criarRefreshTokenValido(usuarioId, tokenRaw);
        Usuario usuarioInativo = new Usuario(usuarioId, "João", "joao123", "joao@email.com",
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(rt));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioInativo));

        assertThatThrownBy(() -> useCase.execute(tokenRaw))
                .isInstanceOf(UsuarioNotFoundException.class);
    }
}
