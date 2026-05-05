package com.roman.application.usuario;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private BuscarUsuarioUseCase useCase;

    private Usuario criarUsuarioAtivo(UUID id) {
        return new Usuario(id, "João Silva", "joao123", "joao@email.com",
                "11999999999", "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    void deve_buscar_usuario_por_id_com_sucesso() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        Usuario resultado = useCase.execute(usuarioId);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioId);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getUsername()).isEqualTo("joao123");
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(usuarioId))
                .isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_inativo() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuarioInativo = new Usuario(usuarioId, "João", "joao123", "joao@email.com",
                null, "hash123", PerfilUsuario.USUARIO, false,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioInativo));

        assertThatThrownBy(() -> useCase.execute(usuarioId))
                .isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void deve_retornar_usuario_ativo_correto() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = criarUsuarioAtivo(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        Usuario resultado = useCase.execute(usuarioId);

        assertThat(resultado.isAtivo()).isTrue();
        assertThat(resultado.getDeletedAt()).isNull();
    }
}
