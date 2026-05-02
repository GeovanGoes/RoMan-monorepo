package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.ParticipanteJaVinculadoException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VincularParticipanteUseCaseTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private ParticipanteRepository participanteRepository;
    @Mock private EventoParticipanteRepository eventoParticipanteRepository;

    @InjectMocks
    private VincularParticipanteUseCase useCase;

    @Test
    void deve_vincular_participante_ao_evento() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(participanteRepository.findById(participanteId)).thenReturn(
                java.util.Optional.of(com.roman.domain.entity.Participante.criar("João", "joao")));
        when(eventoParticipanteRepository.existsByEventoIdAndParticipanteId(eventoId, participanteId)).thenReturn(false);
        when(eventoParticipanteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventoParticipante result = useCase.execute(eventoId, participanteId, false);

        assertThat(result.getEventoId()).isEqualTo(eventoId);
        assertThat(result.getParticipanteId()).isEqualTo(participanteId);
        assertThat(result.isMenorDeIdade()).isFalse();
    }

    @Test
    void deve_vincular_participante_menor_de_idade() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(participanteRepository.findById(participanteId)).thenReturn(
                java.util.Optional.of(com.roman.domain.entity.Participante.criar("Maria", "maria")));
        when(eventoParticipanteRepository.existsByEventoIdAndParticipanteId(eventoId, participanteId)).thenReturn(false);
        when(eventoParticipanteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventoParticipante result = useCase.execute(eventoId, participanteId, true);

        assertThat(result.isMenorDeIdade()).isTrue();
    }

    @Test
    void deve_lancar_excecao_quando_evento_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(eventoId, UUID.randomUUID(), false))
                .isInstanceOf(EventoNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_participante_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(participanteRepository.findById(participanteId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> useCase.execute(eventoId, participanteId, false))
                .isInstanceOf(ParticipanteNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_participante_ja_vinculado() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(participanteRepository.findById(participanteId)).thenReturn(
                java.util.Optional.of(com.roman.domain.entity.Participante.criar("João", "joao")));
        when(eventoParticipanteRepository.existsByEventoIdAndParticipanteId(eventoId, participanteId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(eventoId, participanteId, false))
                .isInstanceOf(ParticipanteJaVinculadoException.class);
    }
}
