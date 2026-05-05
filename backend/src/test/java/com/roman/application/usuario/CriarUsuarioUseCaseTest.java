package com.roman.application.usuario;

import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.AdminSemEmailException;
import com.roman.domain.exception.EmailJaExisteException;
import com.roman.domain.exception.UsernameJaExisteParaUsuarioException;
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
class CriarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private CriarUsuarioUseCase useCase;

    @Test
    void deve_criar_usuario_com_perfil_usuario_com_sucesso() {
        when(usuarioRepository.existsByUsername("joao123")).thenReturn(false);
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senhaSegura1")).thenReturn("hashSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.execute("João Silva", "joao123", "joao@email.com",
                "11999999999", "senhaSegura1", PerfilUsuario.USUARIO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getUsername()).isEqualTo("joao123");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getPerfil()).isEqualTo(PerfilUsuario.USUARIO);
        assertThat(resultado.getSenhaHash()).isEqualTo("hashSenha");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deve_criar_usuario_admin_com_email_com_sucesso() {
        when(usuarioRepository.existsByUsername("admin123")).thenReturn(false);
        when(usuarioRepository.findByEmail("admin@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senhaSegura1")).thenReturn("hashSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.execute("Admin", "admin123", "admin@email.com",
                null, "senhaSegura1", PerfilUsuario.ADMIN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPerfil()).isEqualTo(PerfilUsuario.ADMIN);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deve_criar_usuario_sem_email_quando_perfil_usuario() {
        when(usuarioRepository.existsByUsername("joao123")).thenReturn(false);
        when(passwordEncoder.encode("senhaSegura1")).thenReturn("hashSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.execute("João", "joao123", null,
                "11999999999", "senhaSegura1", PerfilUsuario.USUARIO);

        assertThat(resultado).isNotNull();
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deve_lancar_excecao_quando_admin_sem_email() {
        assertThatThrownBy(() -> useCase.execute("Admin", "admin123", null,
                null, "senhaSegura1", PerfilUsuario.ADMIN))
                .isInstanceOf(AdminSemEmailException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_admin_com_email_em_branco() {
        assertThatThrownBy(() -> useCase.execute("Admin", "admin123", "  ",
                null, "senhaSegura1", PerfilUsuario.ADMIN))
                .isInstanceOf(AdminSemEmailException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_username_ja_existe() {
        when(usuarioRepository.existsByUsername("joao123")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("João", "joao123", "joao@email.com",
                null, "senhaSegura1", PerfilUsuario.USUARIO))
                .isInstanceOf(UsernameJaExisteParaUsuarioException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_lancar_excecao_quando_email_ja_cadastrado() {
        Usuario usuarioExistente = new Usuario(UUID.randomUUID(), "Outro", "outro123",
                "joao@email.com", null, "hash", PerfilUsuario.USUARIO, false,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now(), null);
        when(usuarioRepository.existsByUsername("joao123")).thenReturn(false);
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuarioExistente));

        assertThatThrownBy(() -> useCase.execute("João", "joao123", "joao@email.com",
                null, "senhaSegura1", PerfilUsuario.USUARIO))
                .isInstanceOf(EmailJaExisteException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deve_codificar_senha_antes_de_salvar() {
        when(usuarioRepository.existsByUsername("joao123")).thenReturn(false);
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senhaSegura1")).thenReturn("hashCodificado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.execute("João", "joao123", "joao@email.com",
                null, "senhaSegura1", PerfilUsuario.USUARIO);

        assertThat(resultado.getSenhaHash()).isEqualTo("hashCodificado");
        verify(passwordEncoder).encode("senhaSegura1");
    }
}
