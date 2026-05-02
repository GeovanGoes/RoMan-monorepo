package com.roman.application.evento;

import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.exception.EventoParticipanteNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import com.roman.domain.repository.EventoParticipanteRepository;
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
class AdicionarExclusaoCategoriaUseCaseTest {

    @Mock private EventoParticipanteRepository epRepository;
    @Mock private CategoriaConsumoRepository categoriaRepository;

    @InjectMocks
    private AdicionarExclusaoCategoriaUseCase useCase;

    @Test
    void deve_adicionar_exclusao_de_categoria() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        EventoParticipante ep = EventoParticipante.criar(eventoId, participanteId, false);

        when(epRepository.findByEventoIdAndParticipanteId(eventoId, participanteId)).thenReturn(Optional.of(ep));
        when(categoriaRepository.existsById(categoriaId)).thenReturn(true);
        when(epRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventoParticipante result = useCase.execute(eventoId, participanteId, categoriaId);

        assertThat(result.excluiCategoria(categoriaId)).isTrue();
    }

    @Test
    void deve_lancar_excecao_quando_vinculo_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteId = UUID.randomUUID();
        when(epRepository.findByEventoIdAndParticipanteId(eventoId, participanteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(eventoId, participanteId, UUID.randomUUID()))
                .isInstanceOf(EventoParticipanteNotFoundException.class);
    }

    @Test
    void deve_lancar_excecao_quando_categoria_nao_existe() {
        UUID eventoId = UUID.randomUUID();
        UUID participanteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        EventoParticipante ep = EventoParticipante.criar(eventoId, participanteId, false);

        when(epRepository.findByEventoIdAndParticipanteId(eventoId, participanteId)).thenReturn(Optional.of(ep));
        when(categoriaRepository.existsById(categoriaId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(eventoId, participanteId, categoriaId))
                .isInstanceOf(CategoriaConsumoNotFoundException.class);
    }
}
