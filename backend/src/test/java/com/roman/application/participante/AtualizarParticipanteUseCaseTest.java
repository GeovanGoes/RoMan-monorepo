package com.roman.application.participante;

import com.roman.domain.entity.Participante;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.exception.UsernameJaExisteException;
import com.roman.domain.repository.ParticipanteRepository;
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
    private ParticipanteRepository repository;

    @InjectMocks
    private AtualizarParticipanteUseCase useCase;

    @Test
    void deve_atualizar_participante_com_sucesso() {
        UUID id = UUID.randomUUID();
        Participante existente = Participante.criar("João", "joao123");
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.existsByUsernameAtivoAndIdNot("joao_novo", id)).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Participante result = useCase.execute(id, "João Atualizado", "joao_novo");

        assertThat(result.getNome()).isEqualTo("João Atualizado");
        assertThat(result.getUsername()).isEqualTo("joao_novo");
    }

    @Test
    void deve_lancar_excecao_quando_participante_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, "João", "joao"))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_novo_username_ja_existe() {
        UUID id = UUID.randomUUID();
        Participante existente = Participante.criar("João", "joao123");
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.existsByUsernameAtivoAndIdNot("outro", id)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(id, "João", "outro"))
                .isInstanceOf(UsernameJaExisteException.class);
    }
}
