package com.roman.application.usuario;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarUsuariosUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ListarUsuariosUseCase useCase;

    private Usuario criarUsuarioAtivo(String nome, String username) {
        return new Usuario(UUID.randomUUID(), nome, username, username + "@email.com",
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void deve_listar_usuarios_ativos_com_sucesso() {
        List<Usuario> usuarios = List.of(
                criarUsuarioAtivo("João Silva", "joao123"),
                criarUsuarioAtivo("Maria Souza", "maria456")
        );

        when(usuarioRepository.findAllAtivos()).thenReturn(usuarios);

        List<Usuario> resultado = useCase.execute();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Usuario::getUsername)
                .containsExactlyInAnyOrder("joao123", "maria456");
        verify(usuarioRepository).findAllAtivos();
    }

    @Test
    void deve_retornar_lista_vazia_quando_nenhum_usuario_ativo() {
        when(usuarioRepository.findAllAtivos()).thenReturn(List.of());

        List<Usuario> resultado = useCase.execute();

        assertThat(resultado).isEmpty();
        verify(usuarioRepository).findAllAtivos();
    }

    @Test
    void deve_delegar_para_repositorio() {
        when(usuarioRepository.findAllAtivos()).thenReturn(List.of());

        useCase.execute();

        verify(usuarioRepository).findAllAtivos();
    }
}
