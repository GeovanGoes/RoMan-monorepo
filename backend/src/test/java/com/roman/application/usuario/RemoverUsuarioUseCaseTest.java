package com.roman.application.usuario;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoverUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RemoverUsuarioUseCase useCase;

    private Usuario criarUsuarioAtivo(UUID id) {
        return new Usuario(id, "João Silva", "joao123", "joao@email.com",
                "11999999999", "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void deve_remover_usuario_com_sucesso() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(usuarioId);

        assertThat(usuario.isAtivo()).isFalse();
        assertThat(usuario.getDeletedAt()).isNotNull();
        verify(usuarioRepository).save(usuario);
        verify(refreshTokenRepository).revokeAllByUsuarioId(usuarioId);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(usuarioId))
                .isInstanceOf(UsuarioNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
        verify(refreshTokenRepository, never()).revokeAllByUsuarioId(any());
    }

    @Test
    void deve_lancar_excecao_quando_usuario_ja_removido() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuarioInativo = new Usuario(usuarioId, "João", "joao123", "joao@email.com",
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioInativo));

        assertThatThrownBy(() -> useCase.execute(usuarioId))
                .isInstanceOf(UsuarioNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_revogar_todos_refresh_tokens_ao_remover_usuario() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(usuarioId);

        verify(refreshTokenRepository).revokeAllByUsuarioId(usuarioId);
    }

    @Test
    void deve_fazer_soft_delete_preservando_dados_do_usuario() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(usuarioId);

        assertThat(usuario.getNome()).isEqualTo("João Silva");
        assertThat(usuario.getUsername()).isEqualTo("joao123");
        assertThat(usuario.getDeletedAt()).isNotNull();
    }
}
