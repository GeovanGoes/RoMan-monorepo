package com.roman.application.auth;

import com.roman.application.port.EmailPort;
import com.roman.domain.entity.PasswordResetToken;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.PasswordResetTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarRecuperacaoSenhaUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository resetTokenRepository;

    @Mock
    private EmailPort emailPort;

    private SolicitarRecuperacaoSenhaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SolicitarRecuperacaoSenhaUseCase(
                usuarioRepository, resetTokenRepository, emailPort,
                "http://localhost:3000");
    }

    private Usuario criarUsuarioAtivo(UUID id, String email) {
        return new Usuario(id, "João Silva", "joao123", email,
                "11999999999", "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void deve_enviar_email_de_recuperacao_quando_usuario_encontrado() {
        UUID usuarioId = UUID.randomUUID();
        String email = "joao@email.com";
        Usuario usuario = criarUsuarioAtivo(usuarioId, email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(resetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(email);

        verify(emailPort).enviarEmailRecuperacaoSenha(eq(email), contains("/auth/redefinir-senha?token="));
        verify(resetTokenRepository).save(any(PasswordResetToken.class));
        verify(resetTokenRepository).invalidateAllByUsuarioId(usuarioId);
    }

    @Test
    void deve_ignorar_silenciosamente_quando_email_nao_encontrado() {
        when(usuarioRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        useCase.execute("inexistente@email.com");

        verify(emailPort, never()).enviarEmailRecuperacaoSenha(any(), any());
        verify(resetTokenRepository, never()).save(any());
    }

    @Test
    void deve_ignorar_silenciosamente_quando_usuario_inativo() {
        String email = "inativo@email.com";
        UUID usuarioId = UUID.randomUUID();
        Usuario usuarioInativo = new Usuario(usuarioId, "Inativo", "inativo123", email,
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioInativo));

        useCase.execute(email);

        verify(emailPort, never()).enviarEmailRecuperacaoSenha(any(), any());
        verify(resetTokenRepository, never()).save(any());
    }

    @Test
    void deve_invalidar_tokens_anteriores_antes_de_criar_novo() {
        UUID usuarioId = UUID.randomUUID();
        String email = "joao@email.com";
        Usuario usuario = criarUsuarioAtivo(usuarioId, email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(resetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(email);

        verify(resetTokenRepository).invalidateAllByUsuarioId(usuarioId);
    }

    @Test
    void deve_gerar_link_com_url_frontend_correto() {
        UUID usuarioId = UUID.randomUUID();
        String email = "joao@email.com";
        Usuario usuario = criarUsuarioAtivo(usuarioId, email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(resetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(email);

        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailPort).enviarEmailRecuperacaoSenha(eq(email), linkCaptor.capture());

        String link = linkCaptor.getValue();
        assertThat(link).startsWith("http://localhost:3000/auth/redefinir-senha?token=");
        assertThat(link.replace("http://localhost:3000/auth/redefinir-senha?token=", ""))
                .isNotBlank();
    }

    @Test
    void deve_ignorar_silenciosamente_quando_usuario_sem_email() {
        String email = "semEmail@email.com";
        UUID usuarioId = UUID.randomUUID();
        // Usuário com email null não passará no filter
        Usuario usuarioSemEmail = new Usuario(usuarioId, "SemEmail", "sememail123", null,
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioSemEmail));

        useCase.execute(email);

        verify(emailPort, never()).enviarEmailRecuperacaoSenha(any(), any());
    }
}
