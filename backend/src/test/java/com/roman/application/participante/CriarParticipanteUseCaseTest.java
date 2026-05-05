package com.roman.application.participante;

import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.UsernameJaExisteException;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarParticipanteUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private CriarParticipanteUseCase useCase;

    @Test
    void deve_criar_convidado_com_sucesso() {
        when(repository.existsByUsernameAtivo("joao123")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = useCase.execute("João Silva", "joao123");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getUsername()).isEqualTo("joao123");
        assertThat(result.getSenhaHash()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getDeletedAt()).isNull();
        verify(repository).save(any(Usuario.class));
    }

    @Test
    void deve_lancar_excecao_quando_username_ja_existe() {
        when(repository.existsByUsernameAtivo("joao123")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("João", "joao123"))
                .isInstanceOf(UsernameJaExisteException.class);

        verify(repository, never()).save(any());
    }
}
