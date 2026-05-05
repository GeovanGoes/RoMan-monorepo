package com.roman.application.participante;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.ParticipanteNotFoundException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarParticipanteUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private BuscarParticipanteUseCase useCase;

    @Test
    void deve_retornar_usuario_ativo_quando_id_existe() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.criarConvidado("Maria Santos", "maria123");
        when(repository.findById(id)).thenReturn(Optional.of(usuario));

        Usuario result = useCase.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Maria Santos");
        assertThat(result.getUsername()).isEqualTo("maria123");
        assertThat(result.isAtivo()).isTrue();
        verify(repository).findById(id);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ParticipanteNotFoundException.class);

        verify(repository).findById(id);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_esta_inativo() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Usuario usuarioRemovido = new Usuario(
                UUID.randomUUID(), "Carlos Lima", "carlos99",
                null, null, null, PerfilUsuario.CONVIDADO, false,
                now, now, now
        );
        when(repository.findById(id)).thenReturn(Optional.of(usuarioRemovido));

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ParticipanteNotFoundException.class);

        verify(repository).findById(id);
    }
}
