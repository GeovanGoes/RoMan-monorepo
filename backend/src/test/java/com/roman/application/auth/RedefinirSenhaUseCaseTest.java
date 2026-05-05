package com.roman.application.auth;

import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.PasswordResetToken;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.SenhaFracaException;
import com.roman.domain.exception.TokenInvalidoException;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.PasswordResetTokenRepository;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedefinirSenhaUseCaseTest {

    @Mock
    private PasswordResetTokenRepository resetTokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private RedefinirSenhaUseCase useCase;

    private Usuario criarUsuarioAtivo(UUID usuarioId) {
        return new Usuario(usuarioId, "João Silva", "joao123", "joao@email.com",
                "11999999999", "hashAntigo", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    private PasswordResetToken criarResetTokenValido(UUID usuarioId, String tokenRaw) {
        return new PasswordResetToken(UUID.randomUUID(), usuarioId,
                LoginUseCase.sha256(tokenRaw),
                LocalDateTime.now().plusHours(1), false, LocalDateTime.now());
    }

    @Test
    void deve_redefinir_senha_com_sucesso() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "reset-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        PasswordResetToken prt = criarResetTokenValido(usuarioId, tokenRaw);
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(resetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(prt));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha1")).thenReturn("novoHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(resetTokenRepository.save(prt)).thenReturn(prt);

        useCase.execute(tokenRaw, "novaSenha1");

        verify(usuarioRepository).save(usuario);
        verify(resetTokenRepository).save(prt);
        verify(refreshTokenRepository).revokeAllByUsuarioId(usuario.getId());
    }

    @Test
    void deve_lancar_excecao_quando_nova_senha_muito_curta() {
        assertThatThrownBy(() -> useCase.execute("qualquerToken", "abc1"))
                .isInstanceOf(SenhaFracaException.class);

        verify(resetTokenRepository, never()).findByTokenHash(any());
    }

    @Test
    void deve_lancar_excecao_quando_nova_senha_sem_digito() {
        assertThatThrownBy(() -> useCase.execute("qualquerToken", "senhaSemDigito"))
                .isInstanceOf(SenhaFracaException.class);

        verify(resetTokenRepository, never()).findByTokenHash(any());
    }

    @Test
    void deve_lancar_excecao_quando_nova_senha_nula() {
        assertThatThrownBy(() -> useCase.execute("qualquerToken", null))
                .isInstanceOf(SenhaFracaException.class);

        verify(resetTokenRepository, never()).findByTokenHash(any());
    }

    @Test
    void deve_lancar_excecao_quando_token_nao_encontrado() {
        String tokenRaw = "token-inexistente";
        String tokenHash = LoginUseCase.sha256(tokenRaw);

        when(resetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(tokenRaw, "novaSenha1"))
                .isInstanceOf(TokenInvalidoException.class);

        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void deve_lancar_excecao_quando_token_ja_usado() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "reset-token-usado";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        PasswordResetToken prtUsado = new PasswordResetToken(UUID.randomUUID(), usuarioId,
                tokenHash, LocalDateTime.now().plusHours(1), true, LocalDateTime.now());

        when(resetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(prtUsado));

        assertThatThrownBy(() -> useCase.execute(tokenRaw, "novaSenha1"))
                .isInstanceOf(TokenInvalidoException.class);

        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void deve_lancar_excecao_quando_token_expirado() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "reset-token-expirado";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        PasswordResetToken prtExpirado = new PasswordResetToken(UUID.randomUUID(), usuarioId,
                tokenHash, LocalDateTime.now().minusHours(1), false, LocalDateTime.now());

        when(resetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(prtExpirado));

        assertThatThrownBy(() -> useCase.execute(tokenRaw, "novaSenha1"))
                .isInstanceOf(TokenInvalidoException.class);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "reset-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        PasswordResetToken prt = criarResetTokenValido(usuarioId, tokenRaw);

        when(resetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(prt));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(tokenRaw, "novaSenha1"))
                .isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void deve_marcar_token_como_usado_apos_redefinir_senha() {
        UUID usuarioId = UUID.randomUUID();
        String tokenRaw = "reset-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);
        PasswordResetToken prt = criarResetTokenValido(usuarioId, tokenRaw);
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(resetTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(prt));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha1")).thenReturn("novoHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(resetTokenRepository.save(prt)).thenReturn(prt);

        useCase.execute(tokenRaw, "novaSenha1");

        org.assertj.core.api.Assertions.assertThat(prt.isUsado()).isTrue();
    }
}
