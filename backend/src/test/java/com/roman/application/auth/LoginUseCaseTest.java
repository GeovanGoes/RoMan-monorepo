package com.roman.application.auth;

import com.roman.application.port.JwtTokenService;
import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.RefreshToken;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.CredenciaisInvalidasException;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(usuarioRepository, refreshTokenRepository,
                passwordEncoder, jwtTokenService, 604800L);
    }

    private Usuario criarUsuarioAtivo() {
        return new Usuario(UUID.randomUUID(), "João Silva", "joao123", "joao@email.com",
                "11999999999", "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void deve_realizar_login_com_sucesso() {
        Usuario usuario = criarUsuarioAtivo();
        when(usuarioRepository.findByUsername("joao123")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta", "hash123")).thenReturn(true);
        when(jwtTokenService.gerarAccessToken(usuario)).thenReturn("access-token");
        when(jwtTokenService.gerarRefreshTokenRaw(usuario.getId())).thenReturn("refresh-token-raw");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        LoginUseCase.LoginResult result = useCase.execute("joao123", "senhaCorreta");

        assertThat(result).isNotNull();
        assertThat(result.usuario()).isEqualTo(usuario);
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshTokenRaw()).isEqualTo("refresh-token-raw");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        when(usuarioRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("inexistente", "qualquerSenha"))
                .isInstanceOf(CredenciaisInvalidasException.class);

        verify(passwordEncoder, never()).matches(any(), any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_usuario_inativo() {
        Usuario usuarioInativo = new Usuario(UUID.randomUUID(), "Maria", "maria123", "maria@email.com",
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        when(usuarioRepository.findByUsername("maria123")).thenReturn(Optional.of(usuarioInativo));

        assertThatThrownBy(() -> useCase.execute("maria123", "senhaCorreta"))
                .isInstanceOf(CredenciaisInvalidasException.class);

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void deve_lancar_excecao_quando_senha_incorreta() {
        Usuario usuario = criarUsuarioAtivo();
        when(usuarioRepository.findByUsername("joao123")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hash123")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute("joao123", "senhaErrada"))
                .isInstanceOf(CredenciaisInvalidasException.class);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void deve_salvar_refresh_token_ao_realizar_login() {
        Usuario usuario = criarUsuarioAtivo();
        when(usuarioRepository.findByUsername("joao123")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta", "hash123")).thenReturn(true);
        when(jwtTokenService.gerarAccessToken(usuario)).thenReturn("access-token");
        when(jwtTokenService.gerarRefreshTokenRaw(usuario.getId())).thenReturn("refresh-token-raw");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute("joao123", "senhaCorreta");

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }
}
