package com.roman.application.auth;

import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.CredenciaisInvalidasException;
import com.roman.domain.exception.SenhaFracaException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarSenhaUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private AlterarSenhaUseCase useCase;

    private Usuario criarUsuarioAtivo(UUID id) {
        return new Usuario(id, "João Silva", "joao123", "joao@email.com",
                "11999999999", "hashAtual", PerfilUsuario.USUARIO, true,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void deve_alterar_senha_com_sucesso() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", "hashAtual")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha1")).thenReturn("novoHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(usuarioId, "senhaAtual", "novaSenha1");

        verify(usuarioRepository).save(usuario);
        verify(refreshTokenRepository).revokeAllByUsuarioId(usuarioId);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(usuarioId, "senhaAtual", "novaSenha1"))
                .isInstanceOf(UsuarioNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
        verify(refreshTokenRepository, never()).revokeAllByUsuarioId(any());
    }

    @Test
    void deve_lancar_excecao_quando_usuario_inativo() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuarioInativo = new Usuario(usuarioId, "João", "joao123", "joao@email.com",
                null, "hashAtual", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioInativo));

        assertThatThrownBy(() -> useCase.execute(usuarioId, "senhaAtual", "novaSenha1"))
                .isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_senha_atual_incorreta() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hashAtual")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(usuarioId, "senhaErrada", "novaSenha1"))
                .isInstanceOf(CredenciaisInvalidasException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_nova_senha_muito_curta() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", "hashAtual")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(usuarioId, "senhaAtual", "abc1"))
                .isInstanceOf(SenhaFracaException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_nova_senha_sem_digito() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", "hashAtual")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(usuarioId, "senhaAtual", "senhaSemDigito"))
                .isInstanceOf(SenhaFracaException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_revogar_todos_refresh_tokens_ao_alterar_senha() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", "hashAtual")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha1")).thenReturn("novoHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(usuarioId, "senhaAtual", "novaSenha1");

        verify(refreshTokenRepository).revokeAllByUsuarioId(usuarioId);
    }
}
