package com.roman.application.participante;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarParticipantesUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private ListarParticipantesUseCase useCase;

    @Test
    void deve_retornar_todos_usuarios_ativos() {
        Usuario convidado = Usuario.criarConvidado("Ana Souza", "ana99");
        Usuario admin = Usuario.criar("Geovan Goes", "geovan", "g@g.com", null, "hash", PerfilUsuario.ADMIN);
        when(repository.findAllAtivos()).thenReturn(List.of(convidado, admin));

        List<Usuario> result = useCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Usuario::getUsername)
                .containsExactlyInAnyOrder("ana99", "geovan");
        verify(repository).findAllAtivos();
    }

    @Test
    void deve_retornar_lista_vazia_quando_nao_ha_usuarios_ativos() {
        when(repository.findAllAtivos()).thenReturn(List.of());

        List<Usuario> result = useCase.execute();

        assertThat(result).isEmpty();
        verify(repository).findAllAtivos();
    }
}
