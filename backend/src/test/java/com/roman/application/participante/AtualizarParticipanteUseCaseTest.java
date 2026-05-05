package com.roman.application.participante;

import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.exception.UsernameJaExisteException;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarParticipanteUseCaseTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private AtualizarParticipanteUseCase useCase;

    @Test
    void deve_atualizar_convidado_com_sucesso() {
        UUID id = UUID.randomUUID();
        Usuario existente = Usuario.criarConvidado("João", "joao123");
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.existsByUsernameAtivoAndIdNot("joao_novo", id)).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = useCase.execute(id, "João Atualizado", "joao_novo");

        assertThat(result.getNome()).isEqualTo("João Atualizado");
        assertThat(result.getUsername()).isEqualTo("joao_novo");
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, "João", "joao"))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_usuario_nao_e_convidado() {
        UUID id = UUID.randomUUID();
        Usuario admin = Usuario.criar("Admin", "admin", "a@a.com", null, "hash", PerfilUsuario.ADMIN);
        when(repository.findById(id)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> useCase.execute(id, "Admin", "admin"))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_novo_username_ja_existe() {
        UUID id = UUID.randomUUID();
        Usuario existente = Usuario.criarConvidado("João", "joao123");
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.existsByUsernameAtivoAndIdNot("outro", id)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(id, "João", "outro"))
                .isInstanceOf(UsernameJaExisteException.class);
    }
}
