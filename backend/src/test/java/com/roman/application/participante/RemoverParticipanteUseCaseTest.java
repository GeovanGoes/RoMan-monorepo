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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoverParticipanteUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private RemoverParticipanteUseCase useCase;

    @Test
    void deve_remover_convidado_com_soft_delete() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.criarConvidado("João", "joao123");
        when(repository.findById(id)).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(id);

        verify(repository).save(argThat(u -> u.getDeletedAt() != null));
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_e_convidado() {
        UUID id = UUID.randomUUID();
        Usuario admin = Usuario.criar("Admin", "admin", "a@a.com", null, "hash", PerfilUsuario.ADMIN);
        when(repository.findById(id)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }
}
