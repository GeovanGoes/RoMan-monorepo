package com.roman.application.participante;

import com.roman.domain.entity.Participante;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.ParticipanteRepository;
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
    private ParticipanteRepository repository;

    @InjectMocks
    private RemoverParticipanteUseCase useCase;

    @Test
    void deve_remover_participante_com_soft_delete() {
        UUID id = UUID.randomUUID();
        Participante participante = Participante.criar("João", "joao123");
        when(repository.findById(id)).thenReturn(Optional.of(participante));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(id);

        verify(repository).save(argThat(p -> p.getDeletedAt() != null));
    }

    @Test
    void deve_lancar_excecao_quando_participante_nao_encontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }
}
