package com.roman.application.usuario;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.AdminSemEmailException;
import com.roman.domain.exception.EmailJaExisteException;
import com.roman.domain.exception.UsuarioNotFoundException;
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
class AtualizarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AtualizarUsuarioUseCase useCase;

    private Usuario criarUsuarioAtivo(UUID id, PerfilUsuario perfil) {
        return new Usuario(id, "Nome Original", "username123", "original@email.com",
                "11999999999", "hash123", perfil, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void deve_atualizar_usuario_com_sucesso() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId, PerfilUsuario.USUARIO);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.execute(usuarioId, "Novo Nome", "novo@email.com", "11888888888");

        assertThat(resultado.getNome()).isEqualTo("Novo Nome");
        assertThat(resultado.getEmail()).isEqualTo("novo@email.com");
        assertThat(resultado.getTelefone()).isEqualTo("11888888888");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(usuarioId, "Novo Nome", "novo@email.com", null))
                .isInstanceOf(UsuarioNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_usuario_inativo() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuarioInativo = new Usuario(usuarioId, "João", "joao123", "joao@email.com",
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioInativo));

        assertThatThrownBy(() -> useCase.execute(usuarioId, "Novo Nome", "novo@email.com", null))
                .isInstanceOf(UsuarioNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_admin_tenta_remover_email() {
        UUID usuarioId = UUID.randomUUID();
        Usuario admin = criarUsuarioAtivo(usuarioId, PerfilUsuario.ADMIN);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> useCase.execute(usuarioId, "Admin", null, null))
                .isInstanceOf(AdminSemEmailException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_admin_tenta_email_em_branco() {
        UUID usuarioId = UUID.randomUUID();
        Usuario admin = criarUsuarioAtivo(usuarioId, PerfilUsuario.ADMIN);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> useCase.execute(usuarioId, "Admin", "  ", null))
                .isInstanceOf(AdminSemEmailException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_email_ja_pertence_a_outro_usuario() {
        UUID usuarioId = UUID.randomUUID();
        UUID outroUsuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId, PerfilUsuario.USUARIO);
        Usuario outroUsuario = criarUsuarioAtivo(outroUsuarioId, PerfilUsuario.USUARIO);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("ocupado@email.com")).thenReturn(Optional.of(outroUsuario));

        assertThatThrownBy(() -> useCase.execute(usuarioId, "Novo Nome", "ocupado@email.com", null))
                .isInstanceOf(EmailJaExisteException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_permitir_manter_proprio_email() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId, PerfilUsuario.USUARIO);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("original@email.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.execute(usuarioId, "Novo Nome", "original@email.com", null);

        assertThat(resultado).isNotNull();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deve_permitir_usuario_sem_email() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId, PerfilUsuario.USUARIO);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.execute(usuarioId, "Novo Nome", null, null);

        assertThat(resultado).isNotNull();
        verify(usuarioRepository).save(usuario);
        verify(usuarioRepository, never()).findByEmail(any());
    }
}
